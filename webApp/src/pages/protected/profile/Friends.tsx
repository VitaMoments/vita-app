import React, { useState, useEffect } from "react";
import Input from "../../../components/input/Input"

import { UserService } from "../../../api/service/UserService"
import type { User } from "../types/user/userDomain";

const Friends: React.FC = () => {
    const [users, setUsers] = useState<User[]>({});
    const [query, setQuery] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null);
    const [offset, setOffset] = useState(0)

    useEffect(() => {
        let cancelled = false;

        const load = async () => {
          setLoading(true);
          setError(null);
          try {
            const data = await UserService.searchUsers({
              query: query.trim() || undefined,
              offset: offset,
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
    }, [query, offset]);

    return (
        <div>
            <Input
                name="query"
                value={query}
                onChange={(e) => { setQuery(e.target.value); setOffset(0); }}
                placeholder="Search..." />
        </div>
    );
}

export default Friends;