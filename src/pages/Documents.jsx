import React, { useState, useEffect } from 'react';
import { documentService } from '../services/api';
import { PlusIcon, MagnifyingGlassIcon, FunnelIcon, DocumentIcon, FolderIcon } from '@heroicons/react/24/outline';

export default function Documents() {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [folders] = useState([
    {
      id: 1,
      name: 'Reports',
      items: 12,
      lastModified: '2024-03-27',
    },
    {
      id: 2,
      name: 'Contracts',
      items: 8,
      lastModified: '2024-03-26',
    },
    {
      id: 3,
      name: 'Catalogs',
      items: 5,
      lastModified: '2024-03-25',
    },
  ]);

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    try {
      const response = await documentService.getAllDocuments();
      setDocuments(response.data);
    } catch (err) {
      setError('Failed to fetch documents');
      console.error('Error fetching documents:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-semibold text-gray-900 dark:text-white">Documents</h1>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center space-x-2">
          <PlusIcon className="h-5 w-5" />
          <span>Upload Document</span>
        </button>
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow">
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <div className="flex items-center space-x-4">
            <div className="flex-1">
              <div className="relative">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="Search documents..."
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                />
              </div>
            </div>
            <button className="p-2 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700">
              <FunnelIcon className="h-5 w-5 text-gray-500 dark:text-gray-400" />
            </button>
          </div>
        </div>

        <div className="p-6">
          <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Folders</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {folders.map((folder) => (
              <div
                key={folder.id}
                className="flex items-center p-4 bg-gray-50 dark:bg-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 cursor-pointer"
              >
                <FolderIcon className="h-8 w-8 text-yellow-500" />
                <div className="ml-4">
                  <h3 className="text-sm font-medium text-gray-900 dark:text-white">{folder.name}</h3>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {folder.items} items • Last modified {folder.lastModified}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="p-6 border-t border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Recent Documents</h2>
          <div className="space-y-4">
            {documents.map((document) => (
              <div
                key={document.id}
                className="flex items-center p-4 bg-gray-50 dark:bg-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 cursor-pointer"
              >
                <DocumentIcon className="h-8 w-8 text-blue-500" />
                <div className="ml-4 flex-1">
                  <h3 className="text-sm font-medium text-gray-900 dark:text-white">{document.title}</h3>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {document.type}
                  </p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    Uploaded by: {document.uploadedBy}
                  </p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    Date: {new Date(document.uploadedAt).toLocaleDateString()}
                  </p>
                </div>
                <div className="text-xs text-gray-500 dark:text-gray-400">
                  {document.category}
                </div>
                <div className="flex space-x-2">
                  <button className="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300">
                    View
                  </button>
                  <button className="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300">
                    Download
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
} 