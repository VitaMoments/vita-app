import React, { useState, useEffect } from "react";
import { UserDto } from "../../../api/types/userType";

const Info: React.FC = () => {
     const [user, setUser] = React.useState<UserDto | null>(null);
     return (
            <div>
                <h1>Info</h1>


                      {/* voorbeeld: laat url zien als je DTO dit veld heeft */}
                      {/* <pre>{JSON.stringify(user, null, 2)}</pre> */}
            </div>
        );
}

export default Info;