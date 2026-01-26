import { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { MdList, MdGroup, MdOutlineAccountCircle } from "react-icons/md";
import type { User } from "../../data/types";

import HeaderLink from "./HeaderLink";
import logoUrl from "../../assets/logo.png";

import { useAuth } from "../../auth/AuthContext";
import styles from "./Header.module.css";

interface HeaderProps {
  user: User | null;
}

const Header = ({ user }: HeaderProps) => {
  const [open, setOpen] = useState(false);
  const headerRef = useRef<HTMLElement | null>(null);

  const navigate = useNavigate();
  const { logout } = useAuth(); // ✅ fix

  const handleLogout = async () => {
    await logout();
    setOpen(false);
    navigate("/"); // optioneel
  };

  useEffect(() => {
    if (!open) return;

    const handleClickOutside = (event: MouseEvent | TouchEvent) => {
      if (!headerRef.current) return;

      if (!headerRef.current.contains(event.target as Node)) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    document.addEventListener("touchstart", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("touchstart", handleClickOutside);
    };
  }, [open]);

  return (
    <header ref={headerRef} className={styles.header}>
      <Link to="/" className={styles.logoLink} onClick={() => setOpen(false)}>
        <img src={logoUrl} className={styles.logo} alt="Logo" />
        <h1 className={styles.appname}>Vita Moments</h1>
      </Link>

      <nav className={`${styles.nav} ${open ? styles.open : ""}`}>
        {!user ? (
          <>
            <Link to="/" className={styles.link} onClick={() => setOpen(false)}>
              Home
            </Link>
            <Link
              to="/login"
              className={styles.link}
              onClick={() => setOpen(false)}
            >
              Login
            </Link>
          </>
        ) : (
          <>
            <HeaderLink
              to="/blogs"
              icon={MdList}
              text="Blogs"
              className={styles.link} // ✅ fix
              onClick={() => setOpen(false)}
            />
            <HeaderLink
              to="/friends"
              icon={MdGroup}
              text="Friends"
              className={styles.link} // ✅ fix
              onClick={() => setOpen(false)}
            />
            <HeaderLink
              to="/profile"
              icon={MdOutlineAccountCircle}
              text="Profile"
              className={styles.link} // ✅ fix
              onClick={() => setOpen(false)}
            />

            {/* Als je logout knop in menu wil */}
            <button
              type="button"
              className={styles.link}
              onClick={handleLogout}
            >
              Logout
            </button>
          </>
        )}
      </nav>

      <button
        type="button"
        className={styles.hamburger}
        onClick={() => setOpen(!open)}
        aria-expanded={open}
        aria-label="Menu"
      >
        ☰
      </button>
    </header>
  );
};

export default Header;
