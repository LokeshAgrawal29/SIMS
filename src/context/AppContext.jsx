import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from './AuthContext';

const AppContext = createContext();

// Create axios instance with default config
export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  }
});

// Add request interceptor to include auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Add response interceptor to handle 403 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 403) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export function AppProvider({ children }) {
  const { token } = useAuth();
  const [inventoryItems, setInventoryItems] = useState([]);
  const [lowStockAlerts, setLowStockAlerts] = useState([]);
  const [expiryAlerts, setExpiryAlerts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchInventoryItems = async () => {
    if (!token) return;
    
    try {
      setIsLoading(true);
      const response = await api.get('/inventory/items');
      setInventoryItems(response.data);
      updateAlerts(response.data);
      setError(null);
    } catch (error) {
      console.error('Error fetching inventory:', error);
      setError(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  const updateAlerts = (items) => {
    const currentDate = new Date();
    const thirtyDaysFromNow = new Date();
    thirtyDaysFromNow.setDate(currentDate.getDate() + 30);

    // Check for low stock alerts
    const lowStock = items.filter(item => item.quantity <= item.lowStockThreshold);
    setLowStockAlerts(lowStock.map(item => ({
      id: item.id,
      type: 'low_stock',
      message: `Low stock alert: ${item.name} (${item.quantity} remaining)`,
      severity: 'warning'
    })));

    // Check for expiry alerts
    const nearExpiry = items.filter(item => {
      const expiryDate = new Date(item.expiryDate);
      return expiryDate <= thirtyDaysFromNow;
    });
    setExpiryAlerts(nearExpiry.map(item => ({
      id: item.id,
      type: 'expiry',
      message: `Expiry alert: ${item.name} expires on ${new Date(item.expiryDate).toLocaleDateString()}`,
      severity: 'error'
    })));
  };

  const addInventoryItem = async (item) => {
    try {
      setIsLoading(true);
      const response = await api.post('/inventory/items', item);
      await fetchInventoryItems();
      return response.data;
    } catch (error) {
      console.error('Error adding item:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const updateInventoryItem = async (id, item) => {
    try {
      setIsLoading(true);
      const response = await api.put(`/inventory/items/${id}`, item);
      await fetchInventoryItems();
      return response.data;
    } catch (error) {
      console.error('Error updating item:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const deleteInventoryItem = async (id) => {
    try {
      setIsLoading(true);
      await api.delete(`/inventory/items/${id}`);
      await fetchInventoryItems();
    } catch (error) {
      console.error('Error deleting item:', error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const getAllAlerts = () => {
    return [...lowStockAlerts, ...expiryAlerts].sort((a, b) => {
      if (a.severity === 'error' && b.severity !== 'error') return -1;
      if (a.severity !== 'error' && b.severity === 'error') return 1;
      return 0;
    });
  };

  const getDashboardStats = () => {
    const totalItems = inventoryItems.length;
    const totalQuantity = inventoryItems.reduce((sum, item) => sum + item.quantity, 0);
    const lowStockCount = lowStockAlerts.length;
    const expiringCount = expiryAlerts.length;
    
    return {
      totalItems,
      totalQuantity,
      lowStockCount,
      expiringCount
    };
  };

  // Fetch items when token changes
  useEffect(() => {
    if (token) {
      fetchInventoryItems();
    } else {
      setInventoryItems([]);
      setLowStockAlerts([]);
      setExpiryAlerts([]);
    }
  }, [token]);

  const value = {
    inventoryItems,
    isLoading,
    error,
    alerts: getAllAlerts(),
    lowStockAlerts,
    expiryAlerts,
    stats: getDashboardStats(),
    addInventoryItem,
    updateInventoryItem,
    deleteInventoryItem,
    fetchInventoryItems,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}
