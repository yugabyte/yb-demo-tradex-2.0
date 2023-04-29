import { makeStyles } from "@material-ui/core";
const useStyles = makeStyles((theme) => ({
  phoneWrapper: {
    background: `linear-gradient(
          ${theme.palette.background.purpleGradient1},
          ${theme.palette.background.purpleGradient2}
        )`,
    display: "flex",
    justifyContent: "center",
    flexDirection: "column",
    height: "100%",
    padding: "10px",
  },
  phone: {
    display: "flex",
    flexDirection: "column",
    alignSelf: "center",
    height: "700px",
    width: "370px",
    justifyContent: "center",
    alignItems: "center",
    boxSizing: "border-box",
    background: "white",
    overflow: "hidden",
    borderRadius: "40px",
  },
}));
export default function Menu({ children }) {
  const classes = useStyles();
  return (
    <>
    
    <aside className="sidenav bg-white navbar navbar-vertical navbar-expand-xs border-0 border-radius-xl my-3 fixed-start ms-4 " id="sidenav-main">
    <div className="sidenav-header">
      <i className="fas fa-times p-3 cursor-pointer text-secondary opacity-5 position-absolute end-0 top-0 d-none d-xl-none" aria-hidden="true" id="iconSidenav"></i>
      <a className="navbar-brand m-0" href=" # " target="_blank">
        <img src="assets/img/tradexlogo.png" className="navbar-brand-img h-100" alt="main_logo"/>
       
      </a>
    </div>
    <hr className="horizontal dark mt-0"/>
    <div className="collapse navbar-collapse  w-auto h-auto" id="sidenav-collapse-main">
      <ul className="navbar-nav">
       
        <li className="nav-item">
          <a data-bs-toggle="collapse" href="#pagesExamples" className="nav-link " aria-controls="pagesExamples" role="button" aria-expanded="false">
            <div className="icon icon-shape icon-sm text-center d-flex align-items-center justify-content-center">
              <i className="ni ni-ungroup text-warning text-sm opacity-10"></i>
            </div>
            <span className="nav-link-text ms-1">Dashboard</span>
          </a>
          <div className="collapse " id="pagesExamples">
            <ul className="nav ms-4">
              <li className="nav-item ">
                <a className="nav-link " data-bs-toggle="collapse" aria-expanded="false" href="#profileExample">
                  <span className="sidenav-mini-icon"> P </span>
                  <span className="sidenav-normal"> Profile <b className="caret"></b></span>
                </a>
                <div className="collapse " id="profileExample">
                  <ul className="nav nav-sm flex-column">
                    <li className="nav-item">
                      <a className="nav-link " href="#">
                        <span className="sidenav-mini-icon text-xs"> P </span>
                        <span className="sidenav-normal">User Profile Overview </span>
                      </a>
                    </li>
                    <li className="nav-item">
                      <a className="nav-link " href="#">
                        <span className="sidenav-mini-icon text-xs"> T </span>
                        <span className="sidenav-normal"> Notifications </span>
                      </a>
                    </li>
                    <li className="nav-item">
                      <a className="nav-link " href="#">
                        <span className="sidenav-mini-icon text-xs"> A </span>
                        <span className="sidenav-normal"> Languages </span>
                      </a>
                    </li>
                  </ul>
                </div>
              </li>
              
            </ul>
          </div>
        </li>
      </ul>
    </div>
  </aside>
    
    
    </>
  );
}
