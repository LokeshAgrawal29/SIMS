import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    withCredentials: true  // Set to true to allow cookies
});

// Add request interceptor to add auth token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor to handle auth errors
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 403 || error.response?.status === 401) {
            // Clear token and redirect to login if unauthorized
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// Auth Service
export const authService = {
    login: (credentials) => api.post('/users/login', credentials),
    register: (userData) => api.post('/users/register', userData),
    logout: () => {
        localStorage.removeItem('token');
        return Promise.resolve();
    },
    getCurrentUser: () => api.get('/users/me')
};

// User Service
export const userService = {
    getAllUsers: () => api.get('/users/all'),
    getUser: (id) => api.get(`/users/${id}`),
    createUser: (userData) => api.post('/users', userData),
    updateUser: (id, userData) => api.put(`/users/${id}`, userData),
    deleteUser: (id) => api.delete(`/users/${id}`),
    updateProfile: (userData) => api.put('/users/profile', userData),
    updateUserRole: (id, role) => api.put(`/users/${id}/role?newRole=${role}`),
};

// Inventory Service
export const inventoryService = {
    getAllItems: () => api.get('/inventory/all'),
    getItem: (id) => api.get(`/inventory/${id}`),
    createItem: (item) => api.post('/inventory', item),
    updateItem: (id, item) => api.put(`/inventory/${id}`, item),
    deleteItem: (id) => api.delete(`/inventory/${id}`),
    getLowStockItems: () => api.get('/inventory/low-stock'),
    getExpiringItems: () => api.get('/inventory/expiring'),
};

// Product Service
export const productService = {
    getAllProducts: () => api.get('/products/all'),
    getProduct: (id) => api.get(`/products/${id}`),
    createProduct: (product) => api.post('/products', product),
    updateProduct: (id, product) => api.put(`/products/${id}`, product),
    deleteProduct: (id) => api.delete(`/products/${id}`),
};

// Alert Service
export const alertService = {
    getUnreadAlerts: () => api.get('/alerts/unread'),
    getAllAlerts: () => api.get('/alerts/all'),
    markAlertAsRead: (id) => api.put(`/alerts/${id}/read`),
    getAlertsByType: (type) => api.get(`/alerts/type/${type}`),
};

// Invoice Service
export const invoiceService = {
    getAllInvoices: () => api.get('/invoices/all'),
    getInvoice: (id) => api.get(`/invoices/${id}`),
    createInvoice: (invoice) => api.post('/invoices', invoice),
    updateInvoice: (id, invoice) => api.put(`/invoices/${id}`, invoice),
    deleteInvoice: (id) => api.delete(`/invoices/${id}`),
};

// Document Service
export const documentService = {
    getAllDocuments: () => api.get('/documents/all'),
    getDocument: (id) => api.get(`/documents/${id}`),
    uploadDocument: (documentData) => api.post('/documents', documentData),
    updateDocument: (id, documentData) => api.put(`/documents/${id}`, documentData),
    deleteDocument: (id) => api.delete(`/documents/${id}`),
    downloadDocument: (id) => api.get(`/documents/${id}/download`),
};

// Tag Service
export const tagService = {
    getAllTags: () => api.get('/tags/all'),
    getTag: (id) => api.get(`/tags/${id}`),
    createTag: (tagData) => api.post('/tags', tagData),
    updateTag: (id, tagData) => api.put(`/tags/${id}`, tagData),
    deleteTag: (id) => api.delete(`/tags/${id}`),
    getProductsByTag: (id) => api.get(`/tags/${id}/products`),
};

// Report Service
export const reportService = {
    getInventoryReport: () => api.get('/reports/inventory'),
    getSalesReport: () => api.get('/reports/sales'),
    getExpiryReport: () => api.get('/reports/expiry'),
};

export default api; 