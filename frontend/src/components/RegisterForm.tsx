import React, { useState } from "react";
import { register, RegisterRequest } from "../api/auth";

const RegisterForm: React.FC = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<RegisterRequest["role"]>("CUSTOMER");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload: RegisterRequest = { name, email, password, role };
      await register(payload);
      alert("Registered successfully!");
    } catch {
      alert("Error registering");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Name" />
      <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" />
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" />
      <select value={role} onChange={(e) => setRole(e.target.value as RegisterRequest["role"])}>
        <option value="CUSTOMER">Customer</option>
        <option value="EMPLOYEE">Employee</option>
        <option value="ADMIN">Admin</option>
      </select>
      <button type="submit">Register</button>
    </form>
  );
};

export default RegisterForm;