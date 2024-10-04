// src/apiService.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const getRoutes = (params) => {
    return axios.get(`${API_BASE_URL}/routes`, { params });
};

export const addRoute = (route) => {
    return axios.post(`${API_BASE_URL}/routes`, route);
};

export const getRouteById = (id) => {
    return axios.get(`${API_BASE_URL}/routes/${id}`);
};

export const updateRoute = (id, route) => {
    return axios.put(`${API_BASE_URL}/routes/${id}`, route, {
        headers: { 'Content-Type': 'application/xml' },
    });
};

export const deleteRoute = (id) => {
    return axios.delete(`${API_BASE_URL}/routes/${id}`);
};

export const searchRoutesByName = (value) => {
    return axios.get(`${API_BASE_URL}/routes/name/${value}`);
};

export const getRouteWithMaxFrom = () => {
    return axios.get(`${API_BASE_URL}/routes/from/max`);
};

export const getCountOfRoutesWithDistanceLowerThan = (value) => {
    return axios.get(`${API_BASE_URL}/routes/distance/lower/${value}/count`);
};
