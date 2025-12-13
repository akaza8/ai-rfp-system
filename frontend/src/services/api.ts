import type { Rfp, RfpCreateRequest, RpfFromText } from "../types/Rfp";
import type { Vendor, VendorCreateRequest } from "../types/Vendor";
import { proposalInstance, rfpInstance, vendorInstance } from "./axiosInstance";
export const getRfps = async () => {
  const response = await rfpInstance.get<Rfp[]>("");
  if (!response) throw new Error("Failed to fetch RFPs");
  return response.data;
};

export const createRfp = async (rfpData: RfpCreateRequest) => {
  const response = await rfpInstance.post<Rfp>("", rfpData);
  if (!response) throw new Error("Failed to create RFP");
  return response.data;
};

export const createRfpFromText = async (description: string) => {
  const response = await rfpInstance.post<RpfFromText>("/from-text", {
    description,
  });
  if (!response) throw new Error("Failed to create RFP from text");
  return response.data;
};

export const deleteRfp = async (rfpId: number) => {
  const response = await rfpInstance.delete<void>(`/${rfpId}`);
  if (!response) throw new Error("Failed to delete RFP");
  return response.data;
};

export const getVendors = async () => {
  const response = await vendorInstance.get<Vendor[]>("");
  if (!response) throw new Error("Failed to fetch vendors");
  return response.data;
};

export const createVendor = async (vendorData: VendorCreateRequest) => {
  const response = await vendorInstance.post<Vendor>("", vendorData);
  if (!response) throw new Error("Failed to create vendor");
  return response.data;
};

export const deleteVendor = async (vendorId: number) => {
  const response = await vendorInstance.delete<void>(`/${vendorId}`);
  if (!response) throw new Error("Failed to delete vendor");
  return response.data;
};

export const updateVendor = async (id: number, updates: Partial<Vendor>) => {
  const response = await vendorInstance.patch<Vendor>(`/${id}`, updates);
  if (!response) throw new Error("Failed to update vendor");
  return response.data;
};

export const getRfpById = async (rfpId: number) => {
  const response = await rfpInstance.get<Rfp>(`/${rfpId}`);
  if (!response) throw new Error("Failed to get RFP");
  return response.data;
};

export const inviteVendors = async (rfpId: number, vendorIds: number[]) => {
  const response = await rfpInstance.post<void>(`/${rfpId}/send`, {
    vendorIds,
  });
  if (!response) throw new Error("Failed to invite vendors");
  return response.data;
};

export const getVendorInvitations = async (rfpId: number) => {
  const response = await rfpInstance.get<any[]>(`/${rfpId}/invitations`);
  if (!response) throw new Error("Failed to get invitations");
  return response.data;
};

export const resendVendorInvitation = async (invitationId: number) => {
  const response = await rfpInstance.post<void>(
    `/invitations/${invitationId}/resend`
  );
  if (!response) throw new Error("Failed to resend invitation");
  return response.data;
};

export const getProposalsByRfpId = async (rfpId: number) => {
  const response = await proposalInstance.get<any[]>(`/rfp/${rfpId}`);
  if (!response) throw new Error("Failed to get proposals");
  return response.data;
};
