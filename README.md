# Business Partner Risk Analytics Dashboard

## Overview
This project is a risk analytics dashboard for evaluating Business Partner risk.

The application retrieves Business Partner data and calculates a dynamic risk score based on multiple risk indicators. The results are displayed in a Fiori dashboard for easy visualization.

## Architecture

Fiori UI → CAP Java Service → Risk Calculation Logic → Response with Risk Score

The backend enriches incoming Business Partner data by calculating risk scores before returning the response to the frontend.

## Technologies Used

- SAP CAP (Java)
- SAP Fiori / UI5
- OData Services
- SAP Business Application Studio
- SAP BTP (development environment)

## Features

- Business Partner risk scoring
- Weighted risk calculation logic
- Analytics dashboard visualization
- OData service integration
- Mock data simulation for testing
