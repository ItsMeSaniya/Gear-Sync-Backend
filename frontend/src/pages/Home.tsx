import React from "react";
import LoginForm from "../components/LoginForm";
import RegisterForm from "../components/RegisterForm";

const Home: React.FC = () => {
  return (
    <div className="min-h-screen bg-gradient-to-r from-blue-100 to-purple-100 flex items-center justify-center px-4">
      <div className="max-w-6xl w-full bg-white rounded-3xl shadow-2xl p-10 grid md:grid-cols-2 gap-10">
        
        {/* Welcome Section */}
        <div className="flex flex-col justify-center items-center md:items-start text-center md:text-left space-y-4">
          <h1 className="text-5xl font-extrabold text-gray-800 leading-tight">
            Welcome to <span className="text-blue-600">GearSync</span>
          </h1>
          <p className="text-gray-600 text-lg">
            Connect, manage, and grow your services effortlessly. Login or register to get started!
          </p>
          <img
            src="/assets/welcome-illustration.svg"
            alt="Welcome Illustration"
            className="w-3/4 md:w-full mt-6"
          />
        </div>

        {/* Forms Section */}
        <div className="flex flex-col gap-8">
          
          {/* Login Card */}
          <div className="bg-white p-8 rounded-2xl shadow-lg border border-gray-100 hover:shadow-xl transition-shadow duration-300">
            <h2 className="text-2xl font-semibold text-blue-700 mb-6 text-center">Login</h2>
            <LoginForm />
          </div>

          {/* Register Card */}
          <div className="bg-white p-8 rounded-2xl shadow-lg border border-gray-100 hover:shadow-xl transition-shadow duration-300">
            <h2 className="text-2xl font-semibold text-green-700 mb-6 text-center">Register</h2>
            <RegisterForm />
          </div>

        </div>
      </div>
    </div>
  );
};

export default Home;