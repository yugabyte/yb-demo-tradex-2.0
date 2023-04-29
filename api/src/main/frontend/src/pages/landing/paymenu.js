import Button from '@mui/material/Button';
import { Grid } from '@material-ui/core';
import Box from '@mui/material/Box';
import { Link } from 'react-router-dom';


export default function Manu12(props) {
      return (
            <>
                  <Box>
                        <Grid container spacing={2} >
                              <Grid item xs={12} >
                                    <Link to={`../confirmorder`} state={{ confirmorderDetails: props.confirmorderDetails, selectedValue: props.selectedValue, tradeAmount: props.tradeAmount, action: props.action }}>  <Button disabled={ !((props.tradeAmount > 0) && !(props.selectedValue ==''))} variant="contained" size="large" sx={{ backgroundColor: "#7879F1", width: 300, borderRadius: 2, my: 2 }} >Continue</Button>
                                    </Link></Grid>
                        </Grid>
                  </Box>
            </>
      )
}

