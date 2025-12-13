import { Routes, Route, BrowserRouter } from "react-router-dom";
import MainLayout from "./components/MainLayout";
import RfpListPage from "./pages/RfpListPage";
import CreateRfpPage from "./pages/CreateRfpPage";
import VendorListPage from "./pages/VendorListPage";
import RfpDetailsPage from "./pages/RfpDetailsPage";

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<h1>Dashboard Home (Coming Soon)</h1>} />

          <Route path="rfps" element={<RfpListPage />} />

          <Route path="create" element={<CreateRfpPage />} />

          <Route path="vendors" element={<VendorListPage />} />

          <Route path="rfps/:id" element={<RfpDetailsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
