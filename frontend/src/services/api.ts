import type { Rfp, RfpCreateRequest } from "../types/Rfp";
import type { Vendor, VendorCreateRequest } from "../types/Vendor";
import { rfpInstance, vendorInstance } from "./axiosInstance";

export const getRfps = async () => {
  const response = await rfpInstance.get<Rfp[]>("");
  return response.data;
};

export const createRfp = async (rfpData: RfpCreateRequest) => {
  const response = await rfpInstance.post<Rfp>("", rfpData);
  return response.data;
};

export const deleteRfp = async (rfpId: number) => {
  const response = await rfpInstance.delete<void>(`/${rfpId}`);
  return response.data;
};

export const getVendors = async () => {
  const response = await vendorInstance.get<Vendor[]>("");
  return response.data;
};

export const createVendor = async (vendorData: VendorCreateRequest) => {
  const response = await vendorInstance.post<Vendor>("", vendorData);
  return response.data;
};

export const deleteVendor = async (vendorId: number) => {
  const response = await vendorInstance.delete<void>(`/${vendorId}`);
  return response.data;
};

export const updateVendor = async (id: number, updates: Partial<Vendor>) => {
  const response = await vendorInstance.patch<Vendor>(`/${id}`, updates);
  return response.data;
};
