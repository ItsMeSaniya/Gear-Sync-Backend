import React, { useState, useContext } from "react";
import { login } from "../api/auth";
import { AuthContext } from "../context/AuthContext";

const LoginForm: React.FC = () => {
  const { login: saveToken } = useContext(AuthContext)!;
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await login({ email, password });
      saveToken(res.token);
      alert("Login success!");
    } catch {
      alert("Invalid credentials");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" />
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" />
      <button type="submit">Login</button>
    </form>
  );
};

export default LoginForm;