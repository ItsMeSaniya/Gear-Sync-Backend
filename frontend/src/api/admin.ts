// src/api/admin.ts
import api from "./auth";

export interface EmployeeRegisterPayload {
  email: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  // role is optional; server defaults to EMPLOYEE
  role?: "EMPLOYEE";
}

export interface AdminRegisterPayload {
  email: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  // role is optional; server defaults to ADMIN
  role?: "ADMIN";
}

// Create Employee
export const addEmployee = async (payload: EmployeeRegisterPayload) => {
  const res = await api.post("admin/employees", payload);
  return res.data;
};

// Create Admin
export const addAdmin = async (payload: AdminRegisterPayload) => {
  const res = await api.post("admin/admins", payload);
  return res.data;
};

// List employees (AdminController returns simplified DTO with name/email/role)
export const listEmployees = async () => {
  const res = await api.get("admin/employees");
  return Array.isArray(res.data) ? res.data : [];
};