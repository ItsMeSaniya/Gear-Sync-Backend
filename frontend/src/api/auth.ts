import axios from "axios";

const API_URL = "http://localhost:8080/api/auth"; // backend endpoint

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role?: "CUSTOMER" | "EMPLOYEE" | "ADMIN"; // Add this
}

export const login = async (data: LoginRequest) => {
  const res = await axios.post(`${API_URL}/login`, data);
  return res.data; // { token: string }
};

export const register = async (data: RegisterRequest) => {
  const res = await axios.post(`${API_URL}/register`, data);
  return res.data; // saved user
};