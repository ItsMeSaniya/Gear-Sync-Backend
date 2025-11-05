import api from "./auth";

export interface EmployeeStatusUpdateDTO {
  status: string;
  notes?: string;
}

// Appointments assigned to employee
export const listAssignedAppointments = async (): Promise<any[]> => {
  const res = await api.get<any[]>("/employee/appointments");
  return res.data;
};

export const getAssignedAppointment = async (id: number): Promise<any> => {
  const res = await api.get<any>(`/employee/appointments/${id}`);
  return res.data;
};

export const updateAppointmentStatus = async (
  id: number,
  payload: EmployeeStatusUpdateDTO
): Promise<any> => {
  const res = await api.patch<any>(`/employee/appointments/${id}/status`, payload);
  return res.data;
};

// Projects assigned to employee
export const listAssignedProjects = async (): Promise<any[]> => {
  const res = await api.get<any[]>("/employee/projects");
  return res.data;
};

export const getAssignedProject = async (id: number): Promise<any> => {
  const res = await api.get<any>(`/employee/projects/${id}`);
  return res.data;
};

export const updateProjectStatus = async (
  id: number,
  payload: EmployeeStatusUpdateDTO
): Promise<any> => {
  const res = await api.patch<any>(`/employee/projects/${id}/status`, payload);
  return res.data;
};

// Employee time logs
export interface TimeLogRequestDTO {
  appointmentId?: number;
  projectId?: number;
  description: string;
  hours: number;
}

export interface TimeLogUpdateDTO {
  description?: string;
  hours?: number;
}

export const createTimeLog = async (payload: TimeLogRequestDTO): Promise<any> => {
  const res = await api.post<any>("/employee/timelogs", payload);
  return res.data;
};

export const listMyTimeLogs = async (): Promise<any[]> => {
  const res = await api.get<any[]>("/employee/timelogs");
  return res.data;
};

export const updateTimeLog = async (
  id: number,
  payload: TimeLogUpdateDTO
): Promise<any> => {
  const res = await api.put<any>(`/employee/timelogs/${id}`, payload);
  return res.data;
};

export const deleteTimeLog = async (id: number): Promise<void> => {
  await api.delete(`/employee/timelogs/${id}`);
};



