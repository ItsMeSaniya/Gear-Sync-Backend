import React from "react";
import LoginForm from "../components/LoginForm";
import RegisterForm from "../components/RegisterForm";

const Home: React.FC = () => {
  return (
    <div>
      <h2>Welcome</h2>
      <LoginForm />
      <RegisterForm />
    </div>
  );
};

export default Home;