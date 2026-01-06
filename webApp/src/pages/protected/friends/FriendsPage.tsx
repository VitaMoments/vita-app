import React, { useMemo, useState, useEffect } from "react";
import styles from "./FriendsPage.module.css";

import FriendsTab from "./FriendsTab"
import SearchNewFriendsTab from "./SearchNewFriendsTab"
import FriendRequestsTab from "./FriendRequestsTab"

import Tabs, { TabItem }  from "../../../components/tabs/Tabs"

type FriendsTabLabel = "friends" | "search" | "requests" ;

const FriendsPage: React.FC = () => {
    const [activeTab, setActiveTab] = useState<FriendsTabLabel>("friends")

    const tabs: TabItem<FriendsTabLabel>[] = useMemo(
        () => [
          { value: "friends", label: "Friends", content: <FriendsTab isActive={activeTab === "friends"} /> },
          { value: "search", label: "Search friends", content: <SearchNewFriendsTab isActive={activeTab === "search"} /> },
          { value: "requests", label: "Friend requests", content: <FriendRequestsTab isActive={activeTab === "requests"} /> },
        ],
        [activeTab]
      );

  return (
      <div>
        <div className={styles.content}>
           <h1> Friends </h1>
           <Tabs
             tabs={tabs}
             value={activeTab}
             onChange={setActiveTab}
             ariaLabel="friends tabs"
           />
        </div>
      </div>
      )
  }

export default FriendsPage;