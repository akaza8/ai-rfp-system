export interface Rfp{
    warrantyTerms: string;
    paymentTerms: string;
    id:number;
    title:string;
    budget:number;
    deliveryTimelineDays:number;
    items:RfpItem[];
}

export interface RfpItem{
    id: number;
    itemType: string;
    quantity: number;
    requiredSpecs: string;
} 

export interface RfpCreateRequest{
    title:string;
    budget:number;
    deliveryTimelineDays:number;
    paymentTerms?:string;
    warrantyTerms?:string;
    items:{
        itemType: string;
        quantity: number;
        requiredSpecs: string;
    }[];
}

export interface RpfFromText{
    description: string;
}
export interface Proposal {
  totalPrice: number;
  id: number;
  vendorId: number;
  vendorName: string;
  submittedAt?: string;
  status?: 'PENDING' | 'REVIEWED' | 'ACCEPTED' | 'REJECTED';
  notes?: string;
  attachments?: string[];
  termsAndConditions?: string;
  deliveryTimeline?: number;
}

export interface VendorInvitation {
  id: number;
  vendorId: number;
  rfpId: number;
  status: 'INVITED' | 'SENT' | 'FAILED' | 'RESPONDED';
  sentAt: string;
  createdAt?: string;
  updatedAt?: string;
  vendorName?: string;
  vendorEmail?: string;
}