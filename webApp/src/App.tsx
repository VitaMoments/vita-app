// src/App.tsx
import { useLocation } from "react-router-dom";
import styles from "./App.module.css";

import { useAuth } from "./auth/AuthContext";

import Header from "./components/elements/Header";
import Footer from "./components/elements/Footer";

import AppRoutes from "./routes/AppRoutes";

const App = () => {
  const location = useLocation();
  const { user } = useAuth();
  return (
    <div className={styles.container}>
      <Header user={user} />
      <main>
        <AppRoutes />
      </main>
      <Footer />
    </div>
  );
};

export default App;
