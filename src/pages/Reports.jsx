import React, { useMemo, useState } from 'react';
import { useApp } from '../context/AppContext';
import { useAuth } from '../context/AuthContext';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import { ArrowDownTrayIcon, XCircleIcon } from '@heroicons/react/24/outline';

export default function Reports() {
  const { inventoryItems: items, isLoading } = useApp();
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

  // Calculate category distribution
  const categoryData = useMemo(() => {
    const categories = items.reduce((acc, item) => {
      acc[item.category] = (acc[item.category] || 0) + 1;
      return acc;
    }, {});

    return Object.entries(categories).map(([name, value]) => ({
      name,
      value
    }));
  }, [items]);

  // Calculate stock levels
  const stockData = useMemo(() => {
    return items
      .sort((a, b) => b.quantity - a.quantity)
      .slice(0, 10)
      .map(item => ({
        name: item.name,
        quantity: item.quantity,
        threshold: item.lowStockThreshold
      }));
  }, [items]);

  // Calculate expiry distribution
  const expiryData = useMemo(() => {
    const now = new Date();
    const thirtyDays = new Date();
    thirtyDays.setDate(now.getDate() + 30);
    const ninetyDays = new Date();
    ninetyDays.setDate(now.getDate() + 90);

    const distribution = {
      'Expired': 0,
      'Within 30 Days': 0,
      'Within 90 Days': 0,
      'Over 90 Days': 0
    };

    items.forEach(item => {
      const expiryDate = new Date(item.expiryDate);
      if (expiryDate <= now) {
        distribution['Expired']++;
      } else if (expiryDate <= thirtyDays) {
        distribution['Within 30 Days']++;
      } else if (expiryDate <= ninetyDays) {
        distribution['Within 90 Days']++;
      } else {
        distribution['Over 90 Days']++;
      }
    });

    return Object.entries(distribution).map(([name, value]) => ({
      name,
      value
    }));
  }, [items]);

  // Function to download inventory data as CSV
  const downloadAllReports = () => {
    const now = new Date();
    const formattedDate = now.toISOString().split('T')[0];
    const fileName = `inventory_reports_${formattedDate}.csv`;
    
    // Create CSV content with sections
    let csvContent = '';
    
    // SECTION 1: Full Inventory
    csvContent += '===== FULL INVENTORY REPORT =====\n';
    csvContent += 'Name,Category,Quantity,Low Stock Threshold,Expiry Date,Supplier Name,Supplier Email\n';
    
    items.forEach(item => {
      const expiryDate = new Date(item.expiryDate).toLocaleDateString();
      csvContent += `"${item.name}","${item.category}",${item.quantity},${item.lowStockThreshold},"${expiryDate}","${item.supplierName || ''}","${item.supplierEmail || ''}"\n`;
    });
    
    // SECTION 2: Low Stock Items
    csvContent += '\n===== LOW STOCK ITEMS REPORT =====\n';
    csvContent += 'Name,Category,Current Quantity,Low Stock Threshold,Supplier Name,Supplier Email\n';
    
    const lowStockItems = items.filter(item => item.quantity <= item.lowStockThreshold);
    if (lowStockItems.length > 0) {
      lowStockItems.forEach(item => {
        csvContent += `"${item.name}","${item.category}",${item.quantity},${item.lowStockThreshold},"${item.supplierName || ''}","${item.supplierEmail || ''}"\n`;
      });
    } else {
      csvContent += 'No low stock items found\n';
    }
    
    // SECTION 3: Expiry Report
    csvContent += '\n===== EXPIRY REPORT =====\n';
    csvContent += 'Name,Category,Quantity,Expiry Date,Days Until Expiry,Supplier Name\n';
    
    const sortedByExpiry = [...items].sort((a, b) => new Date(a.expiryDate) - new Date(b.expiryDate));
    sortedByExpiry.forEach(item => {
      const expiryDate = new Date(item.expiryDate);
      const daysUntilExpiry = Math.ceil((expiryDate - now) / (1000 * 60 * 60 * 24));
      csvContent += `"${item.name}","${item.category}",${item.quantity},"${expiryDate.toLocaleDateString()}",${daysUntilExpiry},"${item.supplierName || ''}"\n`;
    });
    
    // SECTION 4: Category Summary
    csvContent += '\n===== CATEGORY SUMMARY =====\n';
    csvContent += 'Category,Item Count,Average Quantity\n';
    
    categoryData.forEach(category => {
      const categoryItems = items.filter(item => item.category === category.name);
      const avgQuantity = Math.round(
        categoryItems.reduce((sum, item) => sum + item.quantity, 0) / categoryItems.length
      );
      csvContent += `"${category.name}",${category.value},${avgQuantity}\n`;
    });

    // Create a blob and download link
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', fileName);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (isLoading) {
    return (
      <div className="p-8 text-center text-gray-500">
        Loading report data...
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-semibold text-gray-900">Inventory Reports</h1>
        
        {/* Admin-only CSV download button */}
        {isAdmin && (
          <button
            onClick={downloadAllReports}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            <ArrowDownTrayIcon className="-ml-1 mr-2 h-5 w-5" />
            Download Reports (CSV)
          </button>
        )}
        
        {/* Non-admin message */}
        {!isAdmin && items.length > 0 && (
          <div className="flex items-center text-gray-500">
            <XCircleIcon className="h-5 w-5 mr-2 text-gray-400" />
            <span className="text-sm">Reports download is admin-only</span>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Stock Levels Chart */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-4">Stock Levels</h2>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={stockData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" angle={-45} textAnchor="end" height={70} />
                <YAxis />
                <Tooltip />
                <Bar dataKey="quantity" fill="#0088FE" name="Current Quantity" />
                <Bar dataKey="threshold" fill="#FF8042" name="Low Stock Threshold" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Category Distribution */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-4">Category Distribution</h2>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={categoryData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  label
                >
                  {categoryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Expiry Distribution */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-4">Expiry Distribution</h2>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={expiryData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  label
                >
                  {expiryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Summary Table */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-4">Inventory Summary</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Items</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Avg Quantity</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {categoryData.map((category, index) => {
                  const categoryItems = items.filter(item => item.category === category.name);
                  const avgQuantity = categoryItems.reduce((sum, item) => sum + item.quantity, 0) / categoryItems.length;
                  
                  return (
                    <tr key={index}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{category.name}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{category.value}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{Math.round(avgQuantity)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}