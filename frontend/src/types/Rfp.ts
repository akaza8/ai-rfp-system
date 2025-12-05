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
