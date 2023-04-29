import Button from '@mui/material/Button';
import AddIcon from '@mui/icons-material/Add';
import { Grid } from '@material-ui/core';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import StarIcon from '@mui/icons-material/Star';
import ShareIcon from '@mui/icons-material/Share';
import IconButton from '@mui/material/IconButton';
import Box from '@mui/material/Box';
import { Link } from 'react-router-dom';
import { putJSON } from "../../services/rest.js";
import AppContext from "../../contexts/AppContext";
import { useContext, useState } from "react";

// function getFavIcon(isFav) {
//     return isFav? <StarIcon />: <StarBorderIcon />;
// }

export default function Candlemenu(props) {
    const { handleQueryLogs } = useContext(AppContext);
    const [listFav, setListFav] = useState(JSON.parse(localStorage.getItem('favStocks')));

    const handleFavClick = async () => {
        try {
            let actionDetails = listFav.includes(props.tradeId) ? 'del' : 'Add';
            if (listFav.includes(props.tradeId)) {
                const index = listFav.indexOf(props.tradeId);
                if (index > -1) {
                    listFav.splice(index, 1);
                }
                localStorage.setItem('favStocks', JSON.stringify(listFav));
                setListFav(listFav);
            }
            else {
                listFav.push(props.tradeId);
                localStorage.setItem('favStocks', JSON.stringify(listFav));
                setListFav(listFav);
            }

            const resp = await putJSON("/api/me/fav/" + props.tradeId + "?action=" + actionDetails);
            handleQueryLogs(resp?.queries, resp?.explainResults, resp?.latencyMillis, resp?.connectionInfo);
        }
        catch (e) {
            console.log("error in fetching database nodes", e);
        }
    }

return (
          <>        
                <Box sx={{mx:1}}> 
                    <Grid container spacing={2}>    
                        <Grid item xs={6} className="profilebtn"> 
                            <Button variant="outlined" startIcon={< AddIcon />} size="small" sx={{color:"#000",backgroundColor:"#eee",border:"1px solid #D9D9D9", fontWeight:"900",fontSize:"0.7125rem",textTransform:"capitalize"}}> Add Liquidity</Button>
                        </Grid>
                    
                        <Grid item xs={3}>
                            <Link to={'../payentry'} state={{ tradeDetails:  props}}>  <Button variant="contained" size="small" sx={{backgroundColor:"#7879F1", fontWeight:"900",fontSize:"0.7125rem",textTransform:"capitalize"}}>Trade</Button></Link>
                        </Grid>
                            <IconButton color="primary" aria-label="upload picture" component="label" onClick={handleFavClick} sx={{color:listFav.includes(props.tradeId) ? "yellow" : "#000"}}>
                                { listFav.includes(props.tradeId) ? <StarIcon /> : <StarBorderIcon />}
                            </IconButton>
                            <IconButton color="primary" aria-label="upload picture" component="label" sx={{color:"#000"}}>
                                <ShareIcon />
                            </IconButton>

                    
                            <Grid item xs={1}></Grid>
                            <Grid item xs={1}></Grid>
                    </Grid>
                </Box>
         </>
        )
  }

