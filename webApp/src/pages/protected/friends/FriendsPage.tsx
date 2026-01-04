import React, { useState, useEffect } from "react";
import styles from "./FriendsPage.module.css";

import FriendsTab from "./FriendsTab"
import SearchNewFriendsTab from "./SearchNewFriendsTab"
import IncomingFriendRequestsTab from "./IncomingFriendRequestsTab"

import Tabs, { TabItem }  from "../../../components/tabs/Tabs"

type FriendsTabLabel = "friends" | "search" | "incoming" | "outgoing";

const FriendsPage: React.FC = () => {
    const tabs: TabItem<FriendsTabLabel>[] = [
        { value: "friends", label: "Friends", content: <FriendsTab /> },
        { value: "search", label: "Search friends", content: <SearchNewFriendsTab /> },
        { value: "incoming", label: "Incoming requests", content: <IncomingFriendRequestsTab /> },
        { value: "outgoing", label: "Outgoing requests", content: <div>Outgoing </div> },
    ];

  return (
      <div>
        <div className={styles.content}>
           <h1> Friends </h1>
           <Tabs tabs={tabs} defaultValue="friends" ariaLabel="friends tabs" />
        </div>
      </div>
      )
  }

export default FriendsPage;