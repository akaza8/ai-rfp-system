import React, { useEffect, useState, useCallback, useMemo } from "react";
import { useParams } from "react-router-dom";
import {
  Card,
  Tabs,
  Table,
  Button,
  Tag,
  message,
  Statistic,
  Row,
  Col,
  Space,
  Spin,
  Alert,
  Popconfirm,
} from "antd";
import {
  MailOutlined,
  ReloadOutlined,
  FileTextOutlined,
  TeamOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  EyeOutlined,
} from "@ant-design/icons";
import type { Rfp, Proposal, VendorInvitation } from "../types/Rfp";
import type { Vendor } from "../types/Vendor";
import {
  getProposalsByRfpId,
  getRfpById,
  getVendors,
  inviteVendors,
  getVendorInvitations,
  resendVendorInvitation,
} from "../services/api";

const STATUS_TAGS = {
  SENT: <Tag color="blue">Sent</Tag>,
  INVITED: <Tag color="orange">Invited</Tag>,
  RESPONDED: <Tag color="green">Responded</Tag>,
  FAILED: <Tag color="red">Failed</Tag>,
  PENDING: <Tag color="orange">Pending</Tag>,
  REVIEWED: <Tag color="blue">Under Review</Tag>,
  ACCEPTED: (
    <Tag color="green" icon={<CheckCircleOutlined />}>
      Accepted
    </Tag>
  ),
  REJECTED: (
    <Tag color="red" icon={<CloseCircleOutlined />}>
      Rejected
    </Tag>
  ),
};
const RfpDetailsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [rfp, setRfp] = useState<Rfp | null>(null);
  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [proposals, setProposals] = useState<Proposal[]>([]);
  const [invitations, setInvitations] = useState<VendorInvitation[]>([]);
  const [selectedVendorIds, setSelectedVendorIds] = useState<React.Key[]>([]);
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [refreshingProposals, setRefreshingProposals] = useState(false);
  const [resending, setResending] = useState<number | null>(null);
  const [activeTab, setActiveTab] = useState("1");
  const [tabChangeTimeout, setTabChangeTimeout] = useState<any | null>(null);

  // Enhanced invitations with vendor details
  const enhancedInvitations = useMemo(() => {
    return invitations.map((invitation) => {
      const vendor = vendors.find((v) => v.id === invitation.vendorId);
      return {
        ...invitation,
        vendorName: vendor?.name || "Unknown Vendor",
        vendorEmail: vendor?.email || "N/A",
        // vendorCategory: vendor?.category,
      };
    });
  }, [invitations, vendors]);

  // Enhanced vendors with invitation status
  const enhancedVendors = useMemo(() => {
    return vendors.map((vendor) => {
      const invitation = invitations.find((inv) => inv.vendorId === vendor.id);
      return {
        ...vendor,
        invitationStatus: invitation?.status || null,
        invitationId: invitation?.id,
        sentAt: invitation?.sentAt,
      };
    });
  }, [vendors, invitations]);

  // Initial load
  useEffect(() => {
    if (id) loadInitialData();
  }, [id]);

  // Load all initial data
  const loadInitialData = async () => {
    setLoading(true);
    try {
      const [rfpData, vendorData] = await Promise.all([
        getRfpById(Number(id!)),
        getVendors(),
      ]);
      setRfp(rfpData);
      setVendors(vendorData);

      // Load proposals and invitations in background
      loadProposals();
      loadInvitations();
    } catch (err: any) {
      console.error("Error loading data:", err);
      message.error(err.message || "Failed to load RFP details");
    } finally {
      setLoading(false);
    }
  };

  // Load proposals separately
  const loadProposals = async () => {
    try {
      const proposalsData = await getProposalsByRfpId(Number(id!));
      setProposals(proposalsData || []);
    } catch (err) {
      console.error("Error loading proposals:", err);
    }
  };

  // Load vendor invitations
  const loadInvitations = async () => {
    try {
      const invitationsData = await getVendorInvitations(Number(id!));
      setInvitations(invitationsData || []);
    } catch (err) {
      console.error("Error loading invitations:", err);
    }
  };

  // Refresh only proposals (for Refresh Inbox button)
  const refreshProposals = async () => {
    setRefreshingProposals(true);
    try {
      await loadProposals();
      message.success("Proposals refreshed!");
    } catch (err: any) {
      message.error(err.message || "Failed to refresh proposals");
    } finally {
      setRefreshingProposals(false);
    }
  };

  // Refresh all data
  const refreshAll = async () => {
    setLoading(true);
    try {
      await loadInitialData();
      message.success("All data refreshed!");
    } catch (err: any) {
      message.error(err.message || "Failed to refresh");
    } finally {
      setLoading(false);
    }
  };

  // Send invites to selected vendors
  const handleInvite = async () => {
    if (selectedVendorIds.length === 0) {
      message.warning("Please select at least one vendor");
      return;
    }

    setSending(true);
    try {
      await inviteVendors(Number(id!), selectedVendorIds as number[]);
      message.success(`Invites sent to ${selectedVendorIds.length} vendor(s)!`);

      // Clear selection and refresh invitations
      setSelectedVendorIds([]);
      await loadInvitations();

      // Switch to proposals tab to see progress
      setActiveTab("3");
    } catch (err: any) {
      console.error("Invite error:", err);
      if (err.response?.status === 429) {
        message.error(
          "Too many requests. Please wait before sending more invites."
        );
      } else if (err.response?.status === 400) {
        message.error("Invalid vendor selection. Please try again.");
      } else {
        message.error(
          err.message || "Failed to send invites. Please check your connection."
        );
      }
    } finally {
      setSending(false);
    }
  };

  // Resend invitation for failed emails
  const handleResendInvitation = async (
    invitationId: number,
    vendorId: number
  ) => {
    setResending(invitationId);
    try {
      await resendVendorInvitation(invitationId);
      message.success("Invitation resent successfully!");

      // Refresh invitations to update status
      await loadInvitations();
    } catch (err: any) {
      console.error("Resend error:", err);
      message.error(err.message || "Failed to resend invitation");
    } finally {
      setResending(null);
    }
  };

  // Get invitation status for a vendor
  const getVendorInvitationStatus = (vendorId: number) => {
    const invitation = invitations.find((inv) => inv.vendorId === vendorId);
    if (!invitation) return null;

    return {
      status: invitation.status,
      sentAt: new Date(invitation.sentAt).toLocaleString(),
    };
  };

  // Check if vendor is already invited
  const isVendorInvited = (vendorId: number) => {
    return invitations.some((inv) => inv.vendorId === vendorId);
  };

  // Get status tag color with enhanced logic
  const getStatusTag = useCallback((status: string) => {
    return (
      STATUS_TAGS[status as keyof typeof STATUS_TAGS] || <Tag>Not Invited</Tag>
    );
  }, []);

  // Get vendor status with tooltip
  const getVendorStatusDisplay = (vendor: any) => {
    if (!vendor.invitationStatus) {
      return <Tag color="blue">New</Tag>;
    }

    const statusTag = getStatusTag(vendor.invitationStatus);

    if (vendor.invitationStatus === "FAILED") {
      return (
        <Space>
          <Tag color="red">Failed</Tag>
        </Space>
      );
    }

    return getStatusTag(vendor.invitationStatus);
  };

  // Get proposal status tag
  const getProposalStatusTag = useCallback((status: string) => {
    return (
      STATUS_TAGS[status as keyof typeof STATUS_TAGS] || (
        <Tag color="default">Pending</Tag>
      )
    );
  }, []);

  // Filter out already invited vendors
  const availableVendors = useMemo(() => {
    return enhancedVendors.filter(
      (vendor) =>
        !vendor.invitationStatus || vendor.invitationStatus === "FAILED"
    );
  }, [enhancedVendors]);

  // Filter for failed invitations
  const failedInvitations = useMemo(() => {
    return enhancedInvitations.filter((inv) => inv.status === "FAILED");
  }, [enhancedInvitations]);

  const proposalStats = useMemo(() => {
    const accepted = proposals.filter((p) => p.status === "ACCEPTED").length;
    const pending = proposals.filter((p) => p.status === "PENDING").length;
    const reviewed = proposals.filter((p) => p.status === "REVIEWED").length;
    const rejected = proposals.filter((p) => p.status === "REJECTED").length;

    return { accepted, pending, reviewed, rejected };
  }, [proposals]);
  // --- TAB 1: Overview ---
  const OverviewTab = () => (
    <div>
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Statistic title="Total Budget" value={rfp?.budget} prefix="$" />
        </Col>
        <Col span={8}>
          <Statistic
            title="Timeline"
            value={rfp?.deliveryTimelineDays}
            suffix="Days"
          />
        </Col>
        <Col span={8}>
          <Statistic title="Items Requested" value={rfp?.items?.length || 0} />
        </Col>
      </Row>

      <h3 style={{ marginTop: 24, marginBottom: 16 }}>Required Items</h3>
      <Table
        dataSource={rfp?.items || []}
        rowKey="id"
        pagination={false}
        columns={[
          {
            title: "Item",
            dataIndex: "itemType",
            key: "type",
            width: "25%",
          },
          {
            title: "Quantity",
            dataIndex: "quantity",
            key: "qty",
            width: "15%",
          },
          {
            title: "Specifications",
            dataIndex: "requiredSpecs",
            key: "specs",
            width: "45%",
          },
          // {
          //   title: 'Estimated Hours',
          //   dataIndex: 'estimatedHours',
          //   key: 'hours',
          //   width: '15%',
          //   render: (hours) => hours ? `${hours} hrs` : 'N/A'
          // },
        ]}
        locale={{ emptyText: "No items specified" }}
      />
    </div>
  );

  // --- TAB 2: Invite Vendors ---
  const InviteTab = useMemo(() => {
    const InviteTabComponent = () => {
      return (
        <div>
          <Alert
            message="Invitation Status"
            description={
              <div>
                <div>
                  •{" "}
                  {
                    enhancedInvitations.filter((inv) => inv.status === "SENT")
                      .length
                  }{" "}
                  successfully sent
                </div>
                <div>
                  •{" "}
                  {
                    enhancedInvitations.filter(
                      (inv) => inv.status === "INVITED"
                    ).length
                  }{" "}
                  invited
                </div>
                <div>
                  •{" "}
                  {
                    enhancedInvitations.filter(
                      (inv) => inv.status === "RESPONDED"
                    ).length
                  }{" "}
                  responded
                </div>

                <div>• {availableVendors.length} available to invite</div>
                <div>
                  •{" "}
                  {
                    enhancedInvitations.filter((inv) => inv.status === "FAILED")
                      .length
                  }{" "}
                  failed
                  {enhancedInvitations.filter((inv) => inv.status === "FAILED")
                    .length > 0}
                </div>
              </div>
            }
            type="info"
            showIcon
            style={{ marginBottom: 16 }}
          />

          <div
            style={{
              marginBottom: 16,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <span>Select vendors to email this RFP to:</span>
            <Space>
              <Button
                onClick={() => {
                  const newVendors = availableVendors.filter(
                    (v) => !v.invitationStatus
                  );
                  setSelectedVendorIds(newVendors.map((v) => v.id));
                }}
                disabled={
                  availableVendors.filter((v) => !v.invitationStatus).length ===
                  0
                }
              >
                Select All
              </Button>
              <Button
                onClick={() => {
                  // Select only FAILED vendors
                  const failedVendors = availableVendors.filter(
                    (v) => v.invitationStatus === "FAILED"
                  );
                  setSelectedVendorIds(failedVendors.map((v) => v.id));
                }}
                disabled={
                  availableVendors.filter(
                    (v) => v.invitationStatus === "FAILED"
                  ).length === 0
                }
              >
                Select All Failed
              </Button>
              <Button
                onClick={() => setSelectedVendorIds([])}
                disabled={selectedVendorIds.length === 0}
              >
                Clear Selection
              </Button>
              <Button
                type="primary"
                icon={<MailOutlined />}
                onClick={handleInvite}
                loading={sending}
                disabled={selectedVendorIds.length === 0}
              >
                Send Invites ({selectedVendorIds.length})
              </Button>
            </Space>
          </div>

          <Tabs defaultActiveKey="available" style={{ marginBottom: 16 }}>
            <Tabs.TabPane
              tab={`Available (${availableVendors.length})`}
              key="available"
            >
              <Table
                dataSource={availableVendors}
                rowKey="id"
                rowSelection={{
                  selectedRowKeys: selectedVendorIds,
                  onChange: setSelectedVendorIds,
                }}
                columns={[
                  {
                    title: "Vendor",
                    dataIndex: "name",
                    key: "name",
                    render: (text, record) => (
                      <div>
                        <div style={{ fontWeight: 500 }}>{text}</div>
                        <div style={{ fontSize: 12, color: "#666" }}>
                          {record.email}
                        </div>
                      </div>
                    ),
                  },
                  // {
                  //   title: 'Category',
                  //   dataIndex: 'category',
                  //   key: 'category',
                  //   render: (category) => category || 'N/A'
                  // },
                  // {
                  //   title: 'Contact',
                  //   key: 'contact',
                  //   render: (_, vendor) => vendor.phone || 'N/A'
                  // },
                  {
                    title: "Status",
                    key: "status",
                    render: (_, vendor) => getVendorStatusDisplay(vendor),
                  },
                ]}
                pagination={{ pageSize: 10, simple: true }}
                locale={{ emptyText: "All vendors have been invited" }}
              />
            </Tabs.TabPane>

            <Tabs.TabPane
              tab={`Already Invited (${enhancedInvitations.length})`}
              key="invited"
            >
              <Table
                dataSource={enhancedInvitations}
                rowKey="id"
                columns={[
                  {
                    title: "Vendor",
                    dataIndex: "vendorName",
                    key: "name",
                    render: (text, record) => (
                      <div>
                        <div style={{ fontWeight: 500 }}>{text}</div>
                        <div style={{ fontSize: 12, color: "#666" }}>
                          {record.vendorEmail}
                        </div>
                      </div>
                    ),
                  },
                  // {
                  //   title: 'Category',
                  //   key: 'category',
                  //   render: (_, record) => record.vendorCategory || 'N/A'
                  // },
                  {
                    title: "Status",
                    dataIndex: "status",
                    key: "status",
                  },
                  {
                    title: "Sent At",
                    dataIndex: "sentAt",
                    key: "sentAt",
                    // render: (date) => new Date(date).toLocaleString(),
                  },
                  {
                    title: "Actions",
                    key: "actions",
                    render: (_, record) => {
                      return "-";
                    },
                  },
                ]}
                pagination={{ pageSize: 10, simple: true }}
                locale={{ emptyText: "No vendors invited yet" }}
              />
            </Tabs.TabPane>
          </Tabs>
        </div>
      );
    };
    return React.memo(InviteTabComponent);
  }, [
    availableVendors,
    enhancedInvitations,
    failedInvitations,
    selectedVendorIds,
    sending,
    resending,
    handleInvite,
    handleResendInvitation,
    getStatusTag,
  ]);

  // --- TAB 3: Proposals (Incoming) ---
  const ProposalsTab = useMemo(() => {
    const proposalsTabComponent = () => {
      // Calculate proposal statistics
      const { accepted, pending, reviewed, rejected } = proposalStats;

      // Function to handle proposal actions
      const handleViewProposal = (proposalId: number) => {
        // Navigate to proposal details or show modal
        message.info(`View proposal ${proposalId}`);
      };

      const handleAcceptProposal = (proposalId: number, vendorName: string) => {
        Popconfirm({
          title: "Accept Proposal",
          description: `Are you sure you want to accept the proposal from ${vendorName}?`,
          onConfirm: () => {
            // API call to accept proposal
            message.success(`Proposal from ${vendorName} accepted!`);
            // Refresh proposals
            loadProposals();
          },
          okText: "Accept",
          cancelText: "Cancel",
        });
      };

      const handleRejectProposal = (proposalId: number, vendorName: string) => {
        Popconfirm({
          title: "Reject Proposal",
          description: `Are you sure you want to reject the proposal from ${vendorName}?`,
          onConfirm: () => {
            // API call to reject proposal
            message.success(`Proposal from ${vendorName} rejected.`);
            // Refresh proposals
            loadProposals();
          },
          okText: "Reject",
          cancelText: "Cancel",
        });
      };

      return (
        <div>
          <div
            style={{
              marginBottom: 24,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <div>
              <h3 style={{ margin: 0 }}>Received Proposals</h3>
              <p style={{ margin: 0, color: "#666" }}>
                {proposals.length} proposal(s) received
                {accepted > 0 && ` • ${accepted} accepted`}
                {pending > 0 && ` • ${pending} pending`}
                {reviewed > 0 && ` • ${reviewed} under review`}
                {rejected > 0 && ` • ${rejected} rejected`}
              </p>
            </div>
            <Space>
              <Button
                icon={<ReloadOutlined />}
                onClick={refreshProposals}
                loading={refreshingProposals}
              >
                Refresh
              </Button>
              <Button
                type="primary"
                onClick={() => setActiveTab("2")}
                icon={<MailOutlined />}
              >
                Invite More Vendors
              </Button>
            </Space>
          </div>

          {proposals.length === 0 ? (
            <Alert
              message="No Proposals Yet"
              description="No vendors have submitted proposals yet. Invite vendors to get started."
              type="info"
              showIcon
              action={
                <Button
                  type="primary"
                  size="small"
                  onClick={() => setActiveTab("2")}
                >
                  Invite Vendors
                </Button>
              }
            />
          ) : (
            <Table
              dataSource={proposals}
              rowKey="id"
              columns={[
                {
                  title: "Vendor",
                  dataIndex: "vendorName",
                  key: "vendorName",
                  render: (text, record) => (
                    <div>
                      <div style={{ fontWeight: 500 }}>{text}</div>
                      {/* <div style={{ fontSize: 12, color: "#666" }}>
                        Submitted:{" "}
                        {new Date(record.submittedAt).toLocaleDateString()}
                        {record.deliveryTimeline &&
                          ` • Delivery: ${record.deliveryTimeline} days`}
                      </div> */}
                    </div>
                  ),
                },
                {
                  title: "Price",
                  dataIndex: "totalPrice",
                  key: "totalPrice",
                  sorter: (a, b) => a.totalPrice - b.totalPrice,
                  render: (totalPrice) => (
                    <div style={{ fontWeight: 600, color: "#1890ff" }}>
                      ${totalPrice}
                    </div>
                  ),
                },
                // {
                //   title: "Status",
                //   dataIndex: "status",
                //   key: "status",
                //   filters: [
                //     { text: "Pending", value: "PENDING" },
                //     { text: "Under Review", value: "REVIEWED" },
                //     { text: "Accepted", value: "ACCEPTED" },
                //     { text: "Rejected", value: "REJECTED" },
                //   ],
                //   onFilter: (value, record) => record.status === value,
                //   render: getProposalStatusTag,
                // },
                // {
                //   title: "Notes",
                //   dataIndex: "notes",
                //   key: "notes",
                //   render: (notes) => notes || "-",
                //   ellipsis: true,
                // },
                // {
                //   title: "Actions",
                //   key: "actions",
                //   width: 200,
                //   render: (_, record) => (
                //     <Space>
                //       <Button
                //         size="small"
                //         icon={<EyeOutlined />}
                //         onClick={() => handleViewProposal(record.id)}
                //       >
                //         View
                //       </Button>
                //       {record.status === "PENDING" ||
                //       record.status === "REVIEWED" ? (
                //         <>
                //           <Button
                //             size="small"
                //             type="primary"
                //             ghost
                //             onClick={() =>
                //               handleAcceptProposal(record.id, record.vendorName)
                //             }
                //           >
                //             Accept
                //           </Button>
                //           <Button
                //             size="small"
                //             danger
                //             onClick={() =>
                //               handleRejectProposal(record.id, record.vendorName)
                //             }
                //           >
                //             Reject
                //           </Button>
                //         </>
                //       ) : record.status === "ACCEPTED" ? (
                //         <Tag color="green">Awarded</Tag>
                //       ) : null}
                //     </Space>
                //   ),
                // },
              ]}
              pagination={{
                pageSize: 10,
                simple: true,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total, range) =>
                  `${range[0]}-${range[1]} of ${total} proposals`,
              }}
            />
          )}

          {/* Show invited but not responded vendors */}
          {enhancedInvitations.filter(
            (inv) => inv.status === "SENT" || inv.status === "INVITED"
          ).length > 0 && (
            <Alert
              message="Pending Invitations"
              description={
                <div>
                  {enhancedInvitations
                    .filter(
                      (inv) => inv.status === "SENT" || inv.status === "INVITED"
                    )
                    .map((inv) => (
                      <div key={inv.id} style={{ marginBottom: 4 }}>
                        <strong>{inv.vendorName}</strong> - Invited on{" "}
                        {new Date(inv.sentAt).toLocaleDateString()}
                      </div>
                    ))}
                </div>
              }
              type="info"
              showIcon
              style={{ marginTop: 24 }}
            />
          )}
        </div>
      );
    };
    return React.memo(proposalsTabComponent);
  }, [
    proposals,
    refreshingProposals,
    enhancedInvitations,
    proposalStats,
    getProposalStatusTag,
    loadProposals,
    setActiveTab,
  ]);

  useEffect(() => {
    return () => {
      if (tabChangeTimeout) {
        clearTimeout(tabChangeTimeout);
      }
    };
  }, [tabChangeTimeout]);

  // --- MAIN RENDER ---
  if (loading && !rfp) {
    return (
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "50vh",
        }}
      >
        <Spin size="large" />
      </div>
    );
  }

  return (
    <Card
      title={
        <div>
          <div style={{ fontSize: 20, fontWeight: 500 }}>{rfp?.title}</div>
          {/* <div style={{ fontSize: 14, color: '#666', marginTop: 4 }}>
            ID: {rfp?.id} • Created on {rfp?.createdAt ? new Date(rfp.createdAt).toLocaleDateString() : 'N/A'}
          </div> */}
        </div>
      }
      extra={
        <Space>
          <Button onClick={() => window.history.back()}>Back</Button>
          <Button
            icon={<ReloadOutlined />}
            onClick={refreshAll}
            loading={loading}
          >
            Refresh All
          </Button>
        </Space>
      }
      style={{ minHeight: "80vh" }}
    >
      <Tabs
        activeKey={activeTab}
        onChange={(key) => {
          if (tabChangeTimeout) {
            clearTimeout(tabChangeTimeout);
          }
          const timeout = setTimeout(() => {
            setActiveTab(key);
          }, 0);

          setTabChangeTimeout(timeout);
        }}
        lazy
        items={[
          {
            key: "1",
            label: (
              <span>
                <FileTextOutlined /> Overview
              </span>
            ),
            children: <OverviewTab />,
          },
          {
            key: "2",
            label: (
              <span>
                <TeamOutlined /> Invite Vendors
                {enhancedInvitations.length > 0 && (
                  <Tag color="blue" style={{ fontSize: 11, marginLeft: 8 }}>
                    {
                      enhancedInvitations.filter(
                        (inv) => inv.status !== "FAILED"
                      ).length
                    }
                  </Tag>
                )}
              </span>
            ),
            children: <InviteTab />,
          },
          {
            key: "3",
            label: (
              <span>
                <MailOutlined /> Proposals
                {proposals.length > 0 && (
                  <Space size={4}>
                    <Tag color="green" style={{ fontSize: 11 }}>
                      {proposals.length}
                    </Tag>
                    {proposals.filter((p) => p.status === "ACCEPTED").length >
                      0 && (
                      <Tag color="green" style={{ fontSize: 11 }}>
                        ✓{" "}
                        {
                          proposals.filter((p) => p.status === "ACCEPTED")
                            .length
                        }
                      </Tag>
                    )}
                  </Space>
                )}
              </span>
            ),
            children: <ProposalsTab />,
          },
        ]}
      />
    </Card>
  );
};

export default RfpDetailsPage;
