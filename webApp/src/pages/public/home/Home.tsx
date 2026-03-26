import React from "react";
import appStyles from "../../../App.module.css";
import ImageSection from "./sections/ImageSection";
import RecipesSection from "./sections/RecipesSection";
import FeedsSection from "./sections/FeedsSection";

const PublicHome: React.FC = () => {
  return (
    <>
      <ImageSection />
      <RecipesSection />
      <FeedsSection />
    </>
  );
};

export default PublicHome;
