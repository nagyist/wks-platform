import React, { useState } from 'react';

import CloseIcon from '@mui/icons-material/Close';
import { AppBar, Button, Dialog, IconButton, Slide, Toolbar, TransitionProps } from '@mui/material';
import Typography from '@mui/material/Typography';

import { Form } from '@formio/react';
import Grid from '@mui/material/Grid';
import MainCard from 'components/MainCard';
import { useEffect } from 'react';

const Transition = React.forwardRef(function Transition(
    props: TransitionProps & {
        children: React.ReactElement
    },
    ref: React.Ref<unknown>
) {
    return <Slide direction="up" ref={ref} {...props} />;
});

export const RecordForm = ({ open, recordType, record, handleClose, mode }) => {
    const [form, setForm] = useState(null);
    const [formData, setFormData] = useState(null);

    useEffect(() => {
        setForm(recordType.fields);
        setFormData({
            data: record,
            metadata: {},
            isValid: true
        });
        // eslint-disable-next-line
    }, [record]);

    const save = () => {
        if (mode === 'new') {
            fetch('http://localhost:8081/record/' + recordType.id, {
                method: 'POST',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData.data)
            })
                .then((response) => {
                    handleClose();
                })
                .catch((err) => {
                    console.log(err.message);
                });
        } else {
            fetch('http://localhost:8081/record/' + recordType.id + '/' + record._id.$oid, {
                method: 'PATCH',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData.data)
            })
                .then((response) => {
                    handleClose();
                })
                .catch((err) => {
                    console.log(err.message);
                });
        }
    };

    const deleteRecord = () => {
        fetch('http://localhost:8081/record/' + recordType.id + '/' + record._id.$oid, {
            method: 'DELETE',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((response) => {
                handleClose();
            })
            .catch((err) => {
                console.log(err.message);
            });
    };

    return (
        form &&
        formData && (
            <Dialog fullScreen open={open} onClose={handleClose} TransitionComponent={Transition} disableEnforceFocus={true}>
                {/* disableEnforceFocus: https://mui.com/material-ui/api/modal/#props - if false, formio component forms are unfocusable */}

                <AppBar sx={{ position: 'relative' }}>
                    <Toolbar>
                        <IconButton edge="start" color="inherit" onClick={handleClose} aria-label="close">
                            <CloseIcon />
                        </IconButton>
                        <Typography sx={{ ml: 2, flex: 1 }} component="div">
                            <div>{recordType.id}</div>
                        </Typography>
                        <Button color="inherit" onClick={save}>
                            Save
                        </Button>
                        {!(mode && mode === 'new') && (
                            <Button color="inherit" onClick={deleteRecord}>
                                Delete
                            </Button>
                        )}
                    </Toolbar>
                </AppBar>

                <Grid container spacing={2} sx={{ display: 'flex', flexDirection: 'column' }}>
                    <Grid item xs={12}>
                        <MainCard sx={{ p: 2 }} content={true}>
                            <Form form={form} submission={formData} />
                        </MainCard>
                    </Grid>
                </Grid>
            </Dialog>
        )
    );
};