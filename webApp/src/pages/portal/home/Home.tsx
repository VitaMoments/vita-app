import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { User } from "../../../data/user/userType"
import { useAuth } from "../../../auth/AuthContext";
import styles from "./Home.module.css";
import { TimeListPost } from "../../../api/types/timelinePostType"
import { fetchTimelinePosts } from "../../../api/requests/timelineRequest";
import { TimelinePostCard } from "../../../components/timeline/TimelinePostCard";

import { TimelineInput } from "../../../components/input/TimelineInput"
import { TimelineButtonBar } from "../../../components/buttons/TimelineButtonBar"
import { Button } from "../../../components/buttons/Button"
import { ErrorBanner, WarningBanner, InfoBanner } from "../../../components/banner/InfoBanner"

const LABELS = ["Following","Self", "Groups", "Discovery"] as const;
const LIMIT = 20;

const Home: React.FC = () => {
    const [activeIndex, setActiveIndex] = useState(0);
    const [posts, setPosts] = useState<TimeLinePost[]>([]);
    const [loading, setLoading] = useState(false)
    const {user, logout} = useAuth()
    const [error, setError] = useState<string | null>(null);
    const [warning, setWarning] = useState<string | null>(null);
    const [info, setInfo] = useState<string | null>(null);
    const navigate = useNavigate()

    const loadPosts = async (index: number) => {
        const label = LABELS[index];
        const offset = 0;

        try {
          setLoading(true);
          setError(null);

          const data = await fetchTimelinePosts({
            label,
            offset,
            limit: LIMIT,
          });

          setPosts(data);
        } catch (e) {
          console.error(e);
          setError("Het ophalen van posts is mislukt.");
        } finally {
          setLoading(false);
        }
      };


    const handleTabChange = (index: number) => {
        setActiveIndex(index);
        void loadPosts(index);
    };

    useEffect(() => {
        void loadPosts(0);
    }, []);

   if (loading) return (<div><p>loading...</p></div>);
   return (
       <div className="timeline">
         <ErrorBanner message={error} />
         <WarningBanner message={warning} />
         <InfoBanner message={info} />
         <TimelineInput />
         <TimelineButtonBar
           activeIndex={activeIndex}
           onChange={handleTabChange}
           labels={[...LABELS]}
         />
         {loading && <p>Loading…</p>}
        <ul className="timeline-list">
          {posts.map((post) => (
            <li key={post.uuid}>
              <TimelinePostCard post={post} />
            </li>
          ))}
        </ul>
       </div>
     );
}

export default Home