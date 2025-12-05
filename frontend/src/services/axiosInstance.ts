import axios from "axios";

export const rfpInstance = axios.create({
  baseURL: "http://localhost:8080/api/rfps",
  headers: {
    "Content-Type": "application/json",
  },
});

export const vendorInstance = axios.create({
  baseURL: "http://localhost:8080/api/vendors",
  headers: {
    "Content-Type": "application/json",
  },
});
