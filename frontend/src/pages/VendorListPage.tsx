import React, { useEffect, useState } from 'react';
import { 
  Table, Button, Modal, Form, Input, message, Popconfirm, Space, Card 
} from 'antd';
import { PlusOutlined, DeleteOutlined, UserOutlined, MailOutlined, EditOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getVendors, createVendor, deleteVendor, updateVendor } from '../services/api';
import type { Vendor, VendorCreateRequest } from '../types/Vendor';
import { Delete, LucideDelete } from 'lucide-react';

const VendorListPage: React.FC = () => {

  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [editingKey, setEditingKey] = useState<number | null>(null);
  
  const [form] = Form.useForm();

  useEffect(() => {
    loadVendors();
  }, []);

  const loadVendors = () => {
    setLoading(true);
    getVendors()
      .then(setVendors)
      .catch((err:any) => message.error(err.message))
      .finally(() => setLoading(false));
  };


  const handleCreate = async (values: VendorCreateRequest) => {
    setModalLoading(true);
    try {
      const newVendor = await createVendor(values);
      message.success('Vendor added successfully');
      setVendors([...vendors, newVendor]); 
      setIsModalOpen(false);
      form.resetFields();
    } catch (error: any) {
        if (error.response?.status === 400 && error.response?.data) {
            const backendErrors = error.response.data;
            const formErrors = Object.entries(backendErrors).map(([field, errorValue])=>{
                const errors = Array.isArray(errorValue)?errorValue:[errorValue];
                return {
                    name: field,
                    errors: errors.map((e)=>e),
                };
            });
            form.setFields(formErrors);
        } else {
            message.error('Failed to add vendor');
        }
    } finally {
      setModalLoading(false);
    }
  };


  const handleDelete = async (id: number) => {
    try {
      setLoading(true);
      await deleteVendor(id);
      message.success('Vendor removed');
      setVendors(vendors.filter(v => v.id !== id));
    } catch (error) {
      message.error('Failed to delete'+(error as Error).message);
    }
    finally{
        setLoading(false);
    }
  };

  const handleInlineEdit = async (record: Vendor, field: keyof Vendor, newValue: string | number) => {
    if (newValue === record[field]) return;  

    const updates = { [field]: newValue } as Partial<Vendor>;  
    try {
      const updatedVendor = await updateVendor(record.id, updates);
      message.success(`Vendor ${field} updated!`);
      setVendors(vendors.map(v => v.id === record.id ? updatedVendor : v));  
      setEditingKey(null);  
    } catch (error: any) {
      message.error(`Failed to update: ${error.message}`);
      setEditingKey(null);  
    }
  };

  const editableCellRender = (field: keyof Vendor, record: Vendor) => (
    <div>
      {editingKey === record.id ? (
        <Input
          defaultValue={record[field] as string}
          onBlur={(e) => handleInlineEdit(record, field, e.target.value)}
          onPressEnter={(e) => handleInlineEdit(record, field, e.currentTarget.value)}
          style={{ width: '100%' }}
        />
      ) : (
        <div
          className="editable-cell-value-wrap"
          style={{ paddingRight: 24 }}
          onClick={() => setEditingKey(record.id)}  // Enter edit mode
        >
          {record[field]}
          <EditOutlined style={{ position: 'absolute', right: 0, top: '50%', transform: 'translateY(-50%)', color: '#1890ff' }} />
        </div>
      )}
    </div>
  );

  const columns: ColumnsType<Vendor> = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { 
      title: 'Vendor Name', 
      dataIndex: 'name', 
      key: 'name',
      render: (_, record) => editableCellRender('name', record),
    },
    { 
      title: 'Email', 
      dataIndex: 'email', 
      key: 'email',
      render: (_, record) => editableCellRender('email', record),
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Popconfirm 
          title="Delete Vendor?" 
          description="This cannot be undone."
          onConfirm={() => handleDelete(record.id)}
          okText="Yes" cancelText="No"
        >
          <Button type="link" danger icon={<LucideDelete />}></Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h2>Vendor Management</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsModalOpen(true)}>
          Add Vendor
        </Button>
      </div>

      <Table 
        columns={columns} 
        dataSource={vendors} 
        loading={loading} 
        rowKey="id" 
        pagination={{ pageSize: 5 }} 
      />

      {/* --- CREATE MODAL --- */}
      <Modal
        title="Add New Vendor"
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={null} 
      >
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item 
            label="Company Name" 
            name="name" 
            rules={[{ required: true, message: 'Please enter name' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="e.g. Acme Corp" />
          </Form.Item>

          <Form.Item 
            label="Email Address" 
            name="email" 
            rules={[
                { required: true, message: 'Please enter email' },
                { type: 'email', message: 'Enter a valid email' }
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="contact@acmecorp.com" />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={modalLoading} block>
              Save Vendor
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default VendorListPage;