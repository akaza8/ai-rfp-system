import React, { useState } from 'react';
import {
  DesktopOutlined,
  FileAddOutlined,
  TeamOutlined,
  HomeOutlined,
} from '@ant-design/icons';
import { Layout, Menu, theme } from 'antd';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';

const { Header, Content, Footer, Sider } = Layout;

const MainLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();
  
  const navigate = useNavigate();
  const location = useLocation();
  const items = [
    { key: '/', icon: <HomeOutlined />, label: 'Dashboard' },
    { key: '/rfps', icon: <DesktopOutlined />, label: 'Manage RFPs' },
    { key: '/create', icon: <FileAddOutlined />, label: 'Create New RFP' },
    { key: '/vendors', icon: <TeamOutlined />, label: 'Vendors' },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
        <div style={{ height: 32, margin: 16, background: 'rgba(255, 255, 255, 0.2)', borderRadius: 6 }} />
        <Menu 
            theme="dark" 
            mode="inline" 
            defaultSelectedKeys={['/']}
            selectedKeys={[location.pathname]} 
            items={items} 
            onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }}  />
        <Content style={{ margin: '16px 16px' }}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            {/* The content of the specific page will render here */}
            <Outlet />
          </div>
        </Content>
        <Footer style={{ textAlign: 'center' }}>
          AI RFP System ©2025 Created by Akash
        </Footer>
      </Layout>
    </Layout>
  );
};

export default MainLayout;