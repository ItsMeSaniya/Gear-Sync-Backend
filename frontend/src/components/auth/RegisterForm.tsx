import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Mail,
  Lock,
  Eye,
  EyeOff,
  Phone,
  User,
  ShieldCheck,
  AlertCircle,
  CheckCircle,
  UserPlus,
} from "lucide-react";
import { register, RegisterRequest } from "../../api/auth";

type Role = RegisterRequest["role"]; // "CUSTOMER" | "EMPLOYEE" | "ADMIN"

// Match Home's accent sweep
const ACCENT_GRADIENT = "bg-gradient-to-r from-cyan-400 via-sky-400 to-indigo-400";

interface Props {
  /** Render with full-page Home background; if false, renders just the card */
  fullPage?: boolean;
}

const RegisterForm: React.FC<Props> = ({ fullPage = true }) => {
  const navigate = useNavigate();

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [role, setRole] = useState<Role>("CUSTOMER");

  const [showPw, setShowPw] = useState(false);
  const [showConfirmPw, setShowConfirmPw] = useState(false);

  const [loading, setLoading] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const validate = () => {
    if (!firstName.trim()) return "First name is required.";
    if (!lastName.trim()) return "Last name is required.";
    if (!/^\S+@\S+\.\S+$/.test(email)) return "Enter a valid email address.";
    if (!/^[0-9+\-()\s]{7,20}$/.test(phoneNumber))
      return "Enter a valid phone number.";
    if (password.length < 6) return "Password must be at least 6 characters.";
    if (password !== confirm) return "Passwords do not match.";
    return null;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setSuccessMsg(null);

    const v = validate();
    if (v) {
      setFormError(v);
      return;
    }

    try {
      setLoading(true);
      const payload: RegisterRequest = {
        firstName,
        lastName,
        phoneNumber,
        email,
        password,
        role,
      };
      await register(payload);
      setSuccessMsg("Registered successfully! You can now log in.");
      setTimeout(() => navigate("/login"), 700);
    } catch (error: any) {
      const msg =
        error?.response?.data?.message ||
        error?.message ||
        "Error registering. Please try again.";
      setFormError(msg);
    } finally {
      setLoading(false);
    }
  };

  const Card = (
    <div className="relative">
      {/* soft glow accents */}
      <div className="pointer-events-none absolute -top-14 -left-10 h-32 w-32 rounded-full bg-cyan-400/20 blur-2xl" />
      <div className="pointer-events-none absolute -bottom-12 -right-8 h-32 w-32 rounded-full bg-indigo-400/20 blur-2xl" />

      <div className="relative mx-auto w-full max-w-md rounded-2xl border border-white/10 bg-white/5 backdrop-blur-xl shadow-[0_20px_60px_-10px_rgba(0,0,0,0.6)] p-6">
        <div className="flex items-center gap-3 mb-4">
          <span className={`p-2 rounded-xl ${ACCENT_GRADIENT} shadow-lg shadow-cyan-500/10`}>
            <UserPlus className="w-5 h-5 text-slate-950" />
          </span>
          <div>
            <h1 className="text-xl font-semibold text-white">Create your account</h1>
            <p className="text-sm text-slate-400">Sign up to get started. (Default role is Customer)</p>
          </div>
        </div>

        {formError && (
          <div
            role="alert"
            aria-live="polite"
            className="mb-4 flex items-start gap-2 rounded-lg border border-red-500/30 bg-red-500/10 px-3 py-2 text-sm text-red-300"
          >
            <AlertCircle className="w-4 h-4 mt-0.5" />
            <span>{formError}</span>
          </div>
        )}

        {successMsg && (
          <div
            role="status"
            className="mb-4 flex items-start gap-2 rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-3 py-2 text-sm text-emerald-300"
          >
            <CheckCircle className="w-4 h-4 mt-0.5" />
            <span>{successMsg}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="flex flex-col gap-4 text-slate-300">
          {/* First & Last name */}
          <div className="grid sm:grid-cols-2 gap-4">
            <label className="text-sm">
              First Name
              <div className="mt-1 relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <User className="w-4 h-4" />
                </span>
                <input
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  className="w-full rounded-lg border border-white/10 bg-white/5 px-10 py-3 text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                  placeholder="Jane"
                  required
                />
              </div>
            </label>

            <label className="text-sm">
              Last Name
              <div className="mt-1 relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <User className="w-4 h-4" />
                </span>
                <input
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  className="w-full rounded-lg border border-white/10 bg-white/5 px-10 py-3 text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                  placeholder="Doe"
                  required
                />
              </div>
            </label>
          </div>

          <label className="text-sm">
            Email
            <div className="mt-1 relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <Mail className="w-4 h-4" />
              </span>
              <input
                type="email"
                value={email}
                autoComplete="email"
                onChange={(e) => setEmail(e.target.value)}
                className="w-full rounded-lg border border-white/10 bg-white/5 px-10 py-3 text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                placeholder="you@example.com"
                required
              />
            </div>
          </label>

          <label className="text-sm">
            Phone Number
            <div className="mt-1 relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <Phone className="w-4 h-4" />
              </span>
              <input
                type="tel"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                className="w-full rounded-lg border border-white/10 bg-white/5 px-10 py-3 text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                placeholder="+1 555 123 4567"
                required
              />
            </div>
          </label>

          {/* Passwords */}
          <label className="text-sm">
            Password
            <div className="mt-1 relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <Lock className="w-4 h-4" />
              </span>
              <input
                type={showPw ? "text" : "password"}
                value={password}
                autoComplete="new-password"
                onChange={(e) => setPassword(e.target.value)}
                className="w-full rounded-lg border border-white/10 bg-white/5 px-10 py-3 pr-12 text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                placeholder="Create a password"
                required
                minLength={6}
              />
              <button
                type="button"
                onClick={() => setShowPw((s) => !s)}
                className="absolute right-2 top-1/2 -translate-y-1/2 inline-flex items-center justify-center rounded-md p-2 text-slate-400 hover:text-slate-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300"
                aria-label={showPw ? "Hide password" : "Show password"}
              >
                {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </label>

          <label className="text-sm">
            Confirm Password
            <div className="mt-1 relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <Lock className="w-4 h-4" />
              </span>
              <input
                type={showConfirmPw ? "text" : "password"}
                value={confirm}
                onChange={(e) => setConfirm(e.target.value)}
                className="w-full rounded-lg border border-white/10 bg-white/5 px-10 py-3 pr-12 text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-300"
                placeholder="Repeat your password"
                required
                minLength={6}
              />
              <button
                type="button"
                onClick={() => setShowConfirmPw((s) => !s)}
                className="absolute right-2 top-1/2 -translate-y-1/2 inline-flex items-center justify-center rounded-md p-2 text-slate-400 hover:text-slate-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300"
                aria-label={showConfirmPw ? "Hide password" : "Show password"}
              >
                {showConfirmPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </label>

          {/* Role */}
          <label className="text-sm">
            Role
            <div className="mt-1 relative">
              <select
                value={role}
                onChange={(e) => setRole(e.target.value as Role)}
                className="w-full appearance-none rounded-lg border border-white/10 bg-white/5 px-4 py-3 text-slate-100 focus:outline-none focus:ring-2 focus:ring-cyan-300"
              >
                <option value="CUSTOMER">Customer</option>
                <option value="EMPLOYEE">Employee</option>
                <option value="ADMIN">Admin</option>
              </select>
              {/* decorative check icon */}
              <span className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-slate-400">
                <ShieldCheck className="w-4 h-4" />
              </span>
            </div>
          </label>

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className={`w-full ${ACCENT_GRADIENT} text-slate-950 font-semibold py-3 rounded-lg shadow-lg shadow-cyan-500/20 hover:brightness-110 disabled:opacity-60 disabled:cursor-not-allowed focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-cyan-300`}
          >
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>

        <div className="mt-4 text-sm text-slate-400">
          Already have an account?{" "}
          <Link to="/login" className="text-cyan-300 hover:text-cyan-200 underline-offset-4 hover:underline">
            Log in
          </Link>
        </div>
      </div>
    </div>
  );

  if (!fullPage) return Card;

  return (
    <div className="relative min-h-screen overflow-hidden text-white">
      {/* Background like Home */}
      <div className="absolute inset-0 -z-10">
        <div className="absolute inset-0 bg-gradient-to-b from-slate-950 via-slate-900 to-slate-950" />
        <div
          className="pointer-events-none absolute -top-40 left-1/2 h-[60rem] w-[60rem] -translate-x-1/2 rounded-full opacity-20 blur-3xl"
          style={{
            background:
              "radial-gradient(closest-side, rgba(34,211,238,0.35), transparent 70%)",
          }}
        />
        <div
          className="pointer-events-none absolute top-1/3 right-[-20%] h-[40rem] w-[40rem] rounded-full opacity-15 blur-3xl"
          style={{
            background:
              "radial-gradient(closest-side, rgba(99,102,241,0.35), transparent 70%)",
          }}
        />
        <div
          className="absolute inset-0 opacity-[0.06]"
          style={{
            backgroundImage:
              "linear-gradient(to right, #fff 1px, transparent 1px), linear-gradient(to bottom, #fff 1px, transparent 1px)",
            backgroundSize: "40px 40px",
          }}
        />
      </div>

      <div className="mx-auto max-w-7xl px-6 py-16 grid place-items-center">
        {Card}
      </div>
    </div>
  );
};

export default RegisterForm;