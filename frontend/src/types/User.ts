export interface User {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  role?: "CUSTOMER" | "EMPLOYEE" | "ADMIN";
}
export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role?: "CUSTOMER" | "EMPLOYEE" | "ADMIN"; // Add this
}