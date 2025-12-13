import React, { useEffect, useState } from "react";
import { Table, Tag, Button, Space, Input, Popconfirm, message, Descriptions } from "antd";
import type { ColumnsType } from "antd/es/table";
import type { Rfp, RfpItem } from "../types/Rfp";
import { useNavigate } from "react-router-dom";
import { getRfps, deleteRfp } from "../services/api";
import type { ExpandableConfig } from "antd/es/table/interface";
import { DeleteOutlined, UserOutlined } from "@ant-design/icons";
import { Delete, DeleteIcon, Eye, EyeIcon } from "lucide-react";

const RfpListPage: React.FC = () => {
  const [data, setData] = useState<Rfp[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchText, setSearchText] = useState("");
  const [expandedRowKeys, setExpandedRowKeys] = useState<React.Key[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const response = await getRfps();
      setData(response);
    } catch (error) {
      console.error("Failed to load RFPs:", error);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const handleView = (rfpId: number) => {
    setExpandedRowKeys((prevKeys) => {
      if (prevKeys.includes(rfpId)) {
        return prevKeys.filter((key) => key !== rfpId); 
      } else {
        return [...prevKeys, rfpId]; 
      }
    });
  };

  const expandedRowRender = (record: Rfp) => {
    return (
      <div style={{ padding: 16, background: "#fafafa" }}>
        {/* RFP Core Info */}
        <Descriptions title="RFP Details" bordered column={2} size="small">
          <Descriptions.Item label="Title">{record.title}</Descriptions.Item>
          <Descriptions.Item label="Budget">${record.budget?.toLocaleString()}</Descriptions.Item>
          <Descriptions.Item label="Timeline">{record.deliveryTimelineDays} days</Descriptions.Item>
          <Descriptions.Item label="Payment Terms">{record.paymentTerms || "N/A"}</Descriptions.Item>
          <Descriptions.Item label="Warranty Terms">{record.warrantyTerms || "N/A"}</Descriptions.Item>
        </Descriptions>

        {/* Items List */}
        {record.items && record.items.length > 0 ? (
        <div style={{ marginTop: 16 }}>
          <h4 style={{ marginBottom: 8 }}>Items ({record.items.length})</h4>
          <Table<RfpItem>
            columns={[
              {
                title: "Item Type",
                dataIndex: "itemType",
                key: "itemType",
                width: 150,
              },
              {
                title: "Quantity",
                dataIndex: "quantity",
                key: "quantity",
                width: 80,
                align: "right",
              },
              {
                title: "Required Specs",
                dataIndex: "requiredSpecs",
                key: "requiredSpecs",
                render: (specs) => <span style={{ wordBreak: "break-word" }}>{specs || "N/A"}</span>,
              },
            ]}
            dataSource={record.items}
            rowKey="id"
            size="small"
            pagination={false} 
            bordered
            style={{ background: "white" }} 
          />
        </div>
      ) : (
        <p style={{ color: "#999", marginTop: 8 }}>No items added.</p>
      )}
      </div>
    );
  };

  // NEW: Expandable config
  const expandable: ExpandableConfig<Rfp> = {
    expandedRowRender,
    expandedRowKeys,
    onExpandedRowsChange: (keys) => setExpandedRowKeys([...keys]),
    rowExpandable: (record) => true, 
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchText(event.target.value);
  };

const handleDelete = async (rfpId: number) => {
  try {
    setLoading(true);
    await deleteRfp(rfpId);
    setData((prevData) => prevData.filter((rfp) => rfp.id !== rfpId));
    message.success("RFP deleted successfully");
  } catch (error) {
    console.error("Failed to delete RFP:", error);
    message.error("Failed to delete RFP: " + (error as Error).message);
  } finally {
    setLoading(false);
  }
};

  const columns: ColumnsType<Rfp> = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",

      sorter: (a, b) => a.id - b.id,
      width: 70,
    },
    {
      title: "Title",
      dataIndex: "title",
      key: "title",
      sorter: (a, b) => a.title.localeCompare(b.title),
      render: (text) => <p>{text}</p>,
      width: 200,
    },
    {
      title: "Budget",
      dataIndex: "budget",
      key: "budget",
      sorter: (a, b) => a.budget - b.budget,
      // defaultSortOrder: "descend",
      render: (amount) => <span>${amount.toLocaleString()}</span>,
      width: 150,
    },
    {
      title: "Timeline (Days)",
      dataIndex: "deliveryTimelineDays",
      key: "deliveryTimelineDays",
      sorter: (a, b) => a.deliveryTimelineDays - b.deliveryTimelineDays,
      // defaultSortOrder: "ascend",
      render: (days) => <span>{days} days</span>,
      width: 150,
    },
    {
      title: "Items Count",
      key: "items",
      render: (_, record) => (
        <Tag color="blue">{record.items?.length || 0} Items</Tag>
      ),
      width: 100,
    },
    {
      title: "Action",
      key: "action",

      render: (_, record) => (
        <Space size="middle">
          <Button type="link" onClick={() => navigate(`/rfps/${record.id}`)} icon={<Eye />}></Button>
          <Popconfirm
            title="Delete this RFP?"
            description="Are you sure to delete this RFP?"
            onConfirm={() => handleDelete(record.id)}
            okText="Yes"
            cancelText="No"
          >
          <Button type="link" danger icon={<DeleteIcon />}>
            
          </Button>
          </Popconfirm>
        </Space>
      ),
      width: 150,
    },
  ];

  const filteredData = data.filter((item) =>
    item.title.toLowerCase().includes(searchText.toLowerCase())
  );
  return (
    <div>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: 16,
        }}
      >
        <h2>Request for Proposals</h2>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "center" }}>
          <Button type="primary" onClick={() => navigate("/create")}>
            + New RFP
          </Button>
        </div>
      </div>

      <Input
        placeholder="Search by title"
        value={searchText}
        onChange={handleChange}
        style={{ marginBottom: 16, width: 300 }}
      />

      <Table
        columns={columns}
        dataSource={filteredData}
        loading={loading}
        expandable={expandable}
        rowKey="id"
        pagination={{ pageSize: 10 }}
      />
    </div>
  );
};

export default RfpListPage;


