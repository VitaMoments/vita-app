import { useState, useEffect, useRef } from 'react';
import {Link, useNavigate} from "react-router-dom";
import { MdGroup, MdOutlineAccountCircle } from "react-icons/md"
import { User } from "../../data/user/userType"
import HeaderLink from "./HeaderLink";
import logoUrl from "../../assets/logo.png";

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
            <Link to="/" className={styles.logoLink} onClick={()=> setOpen(false) }>
                <img src={logoUrl} className={styles.logo} alt="Logo" />
                <h1 className={styles.appname}>Vita Moments</h1>
            </Link>
            <nav className={`${styles.nav} ${open ? styles.open : ""}`}>


                {!user ? (
                    <>
                    <Link
                        to="/"
                        className={styles.link}
                        onClick={() => setOpen(false)}>
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
                          to="/portal/friends"
                          icon={MdGroup}
                          text="Friends"
                          onClick={() => setOpen(false)}
                        />
                        <HeaderLink
                          to="/portal/profile"
                          icon={ MdOutlineAccountCircle }
                          text="Profile"
                          onClick={() => setOpen(false)}
                        />
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