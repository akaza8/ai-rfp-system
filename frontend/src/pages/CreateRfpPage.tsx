import React, { useState } from "react";
import {
  Form,
  Input,
  InputNumber,
  Button,
  Card,
  Space,
  message,
  Divider,
  Tabs,
  Alert,
} from "antd";
import {
  MinusCircleOutlined,
  PlusOutlined,
  RobotOutlined,
  FormOutlined,
  ThunderboltFilled,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import type { RfpCreateRequest } from "../types/Rfp";
import { createRfp, createRfpFromText } from "../services/api";

const { TextArea } = Input;

const CreateRfpPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [aiLoading, setAiLoading] = useState(false);
  const [manualForm] = Form.useForm();
  const [aiForm] = Form.useForm();
  const navigate = useNavigate();

  // --- 1. HANDLE MANUAL SUBMIT ---
  const onFinishManual = (values: RfpCreateRequest) => {
    setLoading(true);
    createRfp(values)
      .then(() => {
        message.success("RFP Created Successfully!");
        navigate("/rfps");
      })
      .catch((err: any) => {

        if (err.response?.status === 400 && err.response?.data) {
          const backendErrors = err.response.data;

          const formErrors = Object.entries(backendErrors).map(([field, errorValue]) => {
            const errors = Array.isArray(errorValue) ? errorValue : [errorValue];
            const fieldName = field.includes('.') ? field.split('.') : field;
            
            return {
              name: fieldName,
              errors: errors,
              touched: true, 
              validating: false, 
            };
          });
          
          manualForm.setFields(formErrors);
        
          if (formErrors.length > 0) {
            setTimeout(() => {
              manualForm.scrollToField(formErrors[0].name);
            }, 100);
          }

          message.error("Please check the form for errors.");
        } else {
          message.error("Failed to create: " + err.message);
        }
      })
      .finally(() => {
        setLoading(false);
      });
  };

  // --- 2. HANDLE AI SUBMIT ---
  const onFinishAi = (values: { description: string }) => {
    setAiLoading(true);
    createRfpFromText(values.description)
      .then((createdRfp) => {
        message.success(`Magic! RFP "${createdRfp.description}" generated.`);
        navigate("/rfps");
      })
      .catch((err: any) => {

        if (err.response?.status === 400 && err.response?.data) {
          const backendErrors = err.response.data;

          const formErrors = Object.entries(backendErrors).map(([field, errorValue]) => {
            
            const formField = field === 'prompt' || field === 'text' ? 'description' : field;
            
            const errors = Array.isArray(errorValue) ? errorValue : [errorValue];
            
            return {
              name: formField,
              errors: errors,
              touched: true, // CRITICAL: Mark field as touched
              validating: false,
            };
          });
          aiForm.setFields(formErrors);
          if (formErrors.length > 0) {
            setTimeout(() => {
              aiForm.scrollToField(formErrors[0].name);
            }, 100);
          }
          
          message.error("Please try after some time");
        } else {
          message.error("AI Generation Failed: " + (err.message || "Unknown error"));
        }
      })
      .finally(() => {
        setAiLoading(false);
      });
  };

  // --- RENDER: MANUAL FORM ---
  const ManualForm = () => (
    <Form
      form={manualForm}
      layout="vertical"
      onFinish={onFinishManual}
      initialValues={{ items: [{}] }}
      validateTrigger={["onBlur", "onChange"]}
      onFieldsChange={(changedFields, allFields) => {
      }}
      onValuesChange={(changedValues, allValues) => {
      }}
    >
      <Form.Item
        label="RFP Title"
        name="title"
        rules={[
          { required: true, message: "Please enter a title" },
          { min: 5, max: 100, message: "Title must be between 2 to 100 characters" },
        ]}
        hasFeedback
      >
        <Input placeholder="e.g. Procurement of Office Laptops" size="large" />
      </Form.Item>

      <Space style={{ display: "flex", marginBottom: 8 }} align="baseline">
        <Form.Item
          label="Budget ($)"
          name="budget"
          rules={[{ required: true, message: "Please enter budget" }]}
          hasFeedback
        >
          <InputNumber style={{ width: 200 }} min={0} />
        </Form.Item>

        <Form.Item
          label="Timeline (Days)"
          name="deliveryTimelineDays"
          rules={[{ required: true, message: "Please enter timeline" }]}
          hasFeedback
        >
          <InputNumber style={{ width: 200 }} min={1} />
        </Form.Item>
      </Space>

      <Space style={{ display: "flex", marginBottom: 8 }} align="baseline">
        <Form.Item label="Payment Terms" name="paymentTerms" hasFeedback>
          <Input style={{ width: 300 }} placeholder="e.g. Net 30" />
        </Form.Item>

        <Form.Item label="Warranty Terms" name="warrantyTerms" hasFeedback>
          <Input style={{ width: 300 }} placeholder="e.g. 1 Year Standard" />
        </Form.Item>
      </Space>

      <Divider>Required Items</Divider>

      <Form.List name="items">
        {(fields, { add, remove }) => (
          <>
            {fields.map(({ key, name, ...restField }) => (
              <Space
                key={key}
                style={{ display: "flex", marginBottom: 8 }}
                align="baseline"
              >
                <Form.Item
                  {...restField}
                  name={[name, "itemType"]}
                  rules={[{ required: true, message: "Missing type" }]}
                  hasFeedback
                >
                  <Input placeholder="Item Name (e.g. Laptop)" />
                </Form.Item>

                <Form.Item
                  {...restField}
                  name={[name, "quantity"]}
                  rules={[{ required: true, message: "Missing qty" }]}
                  hasFeedback
                >
                  <InputNumber placeholder="Qty" min={1} />
                </Form.Item>

                <Form.Item
                  {...restField}
                  name={[name, "requiredSpecs"]}
                  rules={[{ required: true, message: "Missing specs" }]}
                  hasFeedback
                >
                  <Input
                    placeholder="Specs (e.g. 16GB RAM)"
                    style={{ width: 300 }}
                  />
                </Form.Item>

                <MinusCircleOutlined
                  onClick={() => remove(name)}
                  style={{ color: "red" }}
                />
              </Space>
            ))}

            <Form.Item>
              <Button
                type="dashed"
                onClick={() => add()}
                block
                icon={<PlusOutlined />}
              >
                Add Item
              </Button>
            </Form.Item>
          </>
        )}
      </Form.List>

      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          loading={loading}
          size="large"
        >
          Submit RFP
        </Button>
      </Form.Item>
    </Form>
  );

  // --- RENDER: AI FORM ---
  const AiForm = () => (
    <Form
      form={aiForm}
      layout="vertical"
      onFinish={onFinishAi}
      validateTrigger={["onBlur", "onChange"]}
      onFieldsChange={(changedFields, allFields) => {
      }}
    >
      <div style={{ maxWidth: 700, margin: "0 auto", paddingTop: 20 }}>
        <Alert
          title="AI Assistant"
          description="Describe your needs in plain English. The AI will extract the title, budget, timeline, and item details automatically."
          type="info"
          showIcon
          icon={<RobotOutlined />}
          style={{ marginBottom: 24 }}
        />
        
        <Form.Item
          name="description"
          label="Requirement Description"
          rules={[{ required: true, message: "Please describe what you need" }]}
          hasFeedback
        >
          <TextArea
            rows={8}
            placeholder="Example: I need 50 high-end laptops for our dev team. Budget is around $100k. Need them delivered in 2 weeks. Specs: 32GB RAM, 1TB SSD..."
            style={{ fontSize: 16 }}
          />
        </Form.Item>
        
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            size="large"
            icon={<ThunderboltFilled />}
            loading={aiLoading}
            style={{
              background: "linear-gradient(135deg, #6253E1, #04BEFE)",
              border: "none",
              marginTop: 10
            }}
            block
          >
            Generate RFP with AI
          </Button>
        </Form.Item>
      </div>
    </Form>
  );

  // --- TAB ITEMS CONFIG ---
  const tabItems = [
    {
      key: "1",
      label: <span><FormOutlined /> Manual Entry</span>,
      children: <ManualForm />,
    },
    {
      key: "2",
      label: <span><RobotOutlined /> Generate with AI</span>,
      children: <AiForm />,
    },
  ];

  return (
    <Card title="Create New RFP" variant="outlined">
      <Tabs 
        defaultActiveKey="1" 
        items={tabItems} 
        destroyInactiveTabPane={true}
      />
    </Card>
  );
};

export default CreateRfpPage;