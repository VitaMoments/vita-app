import React, { useState, useEffect } from "react";
import { UserDto } from "../../../api/types/userType";

import { UserService } from "../../../api/service/UserService"
import type { User } from "../types/user/userDomain";

const Info: React.FC = () => {
    const [user, setUser] = React.useState<UserDto | null>(null);

    const [users, setUsers] = useState<User[]>([]);
    const [query, setQuery] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
          setLoading(true);
          setError(null);
          try {
            const data = await UserService.searchUsers({
              query: query.trim() || undefined,
              offset: 0,
              limit: 20,
            });
            if (!cancelled) setUsers(data);
          } catch (e: any) {
            if (!cancelled) setError(e?.message ?? "Failed to load users");
          } finally {
            if (!cancelled) setLoading(false);
          }
        };

        load();
        return () => {
          cancelled = true;
        };
    }, [query]);

       return (
         <div>
            <p>temp</p>

         </div>
       );
}

export default Info;