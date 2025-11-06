// api/appointments.ts
import api from "./auth";

export interface AppointmentRequest {
  vehicleId: number;
  serviceIds: number[];           // array
  scheduledDateTime: string;      // "YYYY-MM-DDTHH:mm:ss"
  customerNotes?: string;
}

export interface AppointmentUpdateRequest {
  vehicleId?: number;
  serviceIds?: number[];
  scheduledDateTime?: string;
  customerNotes?: string;
}

export interface ServiceSummaryDTO {
  id: number;
  serviceName: string;
  basePrice?: number;
  estimatedDurationMinutes?: number;
  category?: string;
}

export interface MyAppointmentDTO {
  id: number;
  scheduledDateTime: string;
  status: string;
  customerNotes?: string;
  finalCost?: number | null;
  services?: ServiceSummaryDTO[];
  vehicle?: {
    id: number;
    make: string;
    model: string;
    year: number;
    registrationNumber?: string;
  };
}

export const bookAppointment = async (payload: AppointmentRequest) => {
  const res = await api.post("customer/appointments", payload);
  return res.data;
};

export const listMyAppointments = async (): Promise<MyAppointmentDTO[]> => {
  const res = await api.get<MyAppointmentDTO[]>("customer/appointments");
  return res.data;
};

export const getMyAppointment = async (id: number) => {
  const res = await api.get<MyAppointmentDTO>(`customer/appointments/${id}`);
  return res.data;
};

// use PATCH for partial
export const updateMyAppointment = async (id: number, payload: AppointmentUpdateRequest) => {
  const res = await api.patch(`customer/appointments/${id}`, payload);
  return res.data;
};

export const cancelMyAppointment = async (id: number) => {
  const res = await api.put(`customer/appointments/${id}/cancel`);
  return res.data;
};

export const deleteMyAppointment = async (id: number) => {
  await api.delete(`customer/appointments/${id}`);
};