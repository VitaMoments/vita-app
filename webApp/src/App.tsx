// src/App.tsx
import { useLocation } from "react-router-dom";
import styles from "./App.module.css";

import { useAuth } from "./auth/AuthContext";

import Header from "./components/elements/Header";
import Footer from "./components/elements/Footer";

import AppRoutes from "./routes/AppRoutes";

const App = () => {
  const location = useLocation();
  const plainRoutes = ["/login", "/registration", "/blogs"];
  const isPlainPage = plainRoutes.some(
    (p) => location.pathname === p || location.pathname.startsWith(p + "/")
  );

  const { user } = useAuth();

  return (
    <div className={styles.container}>
      <Header user={user} />

      {isPlainPage ? (
        <AppRoutes />
      ) : (
        <main className={styles.main}>
          <AppRoutes />
        </main>
      )}

      <Footer />
    </div>
  );
};

export default App;
