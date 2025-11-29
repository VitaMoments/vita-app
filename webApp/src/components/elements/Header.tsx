import { useState, useEffect, useRef } from 'react';
import {Link, useNavigate} from "react-router-dom";
import { User } from "../../data/user/userType"

import { useAuth } from "../../auth/AuthContext";
import styles from "./Header.module.css"

interface HeaderProps {
  user: User | null;
}

const Header = ({ user }: HeaderProps) => {
    const [open, setOpen] = useState(false);
    const headerRef = useRef<HTMLElement | null>(null);

    const handleLogout = () => {
        logout()
    }

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
            <h1 className={styles.logo}>Health APK Header</h1>
            <nav className={`${styles.nav} ${open ? styles.open : ""}`}>
                <Link
                    to="/"
                    className={styles.link}
                    onClick={() => setOpen(false)}> Home </Link>

                {!user ? (
                    <Link
                        to="/login"
                        className={styles.link}
                        onClick={() => setOpen(false)}
                    >
                        Login
                    </Link>
                ) : (
                    <>

                    <Link
                        to="/portal/profile"
                        className={styles.link}
                        onClick={() => setOpen(false)}
                    >
                        Profile
                    </Link>
                    </>
                )}
            </nav>
            <button className={styles.hamburger} onClick={() => setOpen(!open)}>
                ☰
            </button>
        </header>
    )
}

export default Header;