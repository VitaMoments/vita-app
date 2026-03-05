import React from "react";
import appStyles from "../../../App.module.css";
import ImageSection from "./sections/ImageSection";
import RecipesSection from "./sections/RecipesSection";
import BlogsSection from "./sections/BlogsSection";
import FeedsSection from "./sections/FeedsSection"

const PublicHome: React.FC = () => {
  return (
   <>
    <ImageSection />
    <RecipesSection />
    <BlogsSection />
    <FeedsSection />
   </>
  );
};

export default PublicHome;