import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Home from "./pages/Home";
import AdminDashboard from "./pages/Admin/AdminDashboard";
import EmployeeDashboard from "./pages/Employee/EmployeeDashboard";
import CustomerDashboard from "./pages/Customer/CustomerDashboard";
import UserManagement from "./pages/Admin/UserManagement";
import ProtectedRoute from "./pages/ProtectedRoute";
import { AdminLayout, CustomerLayout, EmployeeLayout } from "./components/layouts";

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Route */}
          <Route path="/" element={<Home />} />

          {/* Admin Routes with Layout */}
          <Route
            path="/admin-dashboard"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <AdminLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<AdminDashboard />} />
            <Route path="users" element={<UserManagement />} />
          </Route>

          {/* Employee Routes with Layout */}
          <Route
            path="/employee-dashboard"
            element={
              <ProtectedRoute requiredRole="EMPLOYEE">
                <EmployeeLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<EmployeeDashboard />} />
          </Route>

          {/* Customer Routes with Layout */}
          <Route
            path="/customer-dashboard"
            element={
              <ProtectedRoute requiredRole="CUSTOMER">
                <CustomerLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<CustomerDashboard />} />
          </Route>

          {/* Optional fallback route */}
          <Route path="*" element={<Home />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;