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
} from "antd";
import { MinusCircleOutlined, PlusOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import type { RfpCreateRequest } from "../types/Rfp";
import { createRfp } from "../services/api";

const CreateRfpPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFinish = (values: RfpCreateRequest) => {
    setLoading(true);
    createRfp(values)
      .then(() => {
        message.success("RFP Created Successfully!");
        navigate("/rfps");
      })
      .catch((err: any) => {
        if (err.response?.status === 400 && err.response?.data) {
         const backendErrors = err.response.data;  
        // console.log('Backend errors:', backendErrors);  
        const formErrors = Object.entries(backendErrors).map(([field, errorValue]) => {
          const errors = Array.isArray(errorValue) ? errorValue : [errorValue];
          return {
            name: field, 
            errors: errors.map((e) => `Backend: ${e}`),  
          };
        });

        form.setFields(formErrors);
        } else {
          message.error("Failed to create: " + err.message);
        }
      })
      .finally(() => setLoading(false));
  };

  return (
    <Card title="Create New RFP" variant="outlined">
      <Form
        form={form}
        layout="vertical"
        onFinish={onFinish}
        initialValues={{ items: [{}] }}
      >
        {/* --- SECTION 1: General Info --- */}
        <Form.Item
          label="RFP Title"
          name="title"
          rules={[{ required: true, message: "Please enter a title" }]}
        >
          <Input
            placeholder="e.g. Procurement of Office Laptops"
            size="large"
          />
        </Form.Item>

        <Space style={{ display: "flex", marginBottom: 8 }} align="baseline">
          <Form.Item
            label="Budget ($)"
            name="budget"
            rules={[{ required: true }]}
          >
            <InputNumber style={{ width: 200 }} min={0} />
          </Form.Item>

          <Form.Item
            label="Timeline (Days)"
            name="deliveryTimelineDays"
            rules={[{ required: true }]}
          >
            <InputNumber style={{ width: 200 }} min={1} />
          </Form.Item>
        </Space>

        <Space style={{ display: "flex", marginBottom: 8 }} align="baseline">
          <Form.Item label="Payment Terms" name="paymentTerms">
            <Input style={{ width: 300 }} placeholder="e.g. Net 30" />
          </Form.Item>

          <Form.Item label="Warranty Terms" name="warrantyTerms">
            <Input style={{ width: 300 }} placeholder="e.g. 1 Year Standard" />
          </Form.Item>
        </Space>

        <Divider titlePlacement="center">Required Items</Divider>

        {/* --- SECTION 2: Dynamic Items List --- */}
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
                  >
                    <Input placeholder="Item Name (e.g. Laptop)" />
                  </Form.Item>

                  <Form.Item
                    {...restField}
                    name={[name, "quantity"]}
                    rules={[{ required: true, message: "Missing qty" }]}
                  >
                    <InputNumber placeholder="Qty" min={1} />
                  </Form.Item>

                  <Form.Item
                    {...restField}
                    name={[name, "requiredSpecs"]}
                    rules={[{ required: true, message: "Missing specs" }]}
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
    </Card>
  );
};

export default CreateRfpPage;
