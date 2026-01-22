import React from 'react';
import ReactDOM from 'react-dom/client';
import App from "./App.tsx";
import {BrowserRouter} from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import "./Index.css";

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
