#!/usr/bin/env python3
"""
Health Backend API Test Script

This script tests the public functionality of the health backend application 
that doesn't require authentication, specifically:
- Registration endpoints
- Search endpoints (clinics, doctors, timetables)

These tests can be run against the application without needing to handle tokens
or authentication sessions.
"""

import requests
import json
import time
from datetime import datetime
import logging
import sys
import random
from typing import Dict, List, Optional, Any, Tuple

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("api_test.log"),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

# API Configuration
BASE_URL = "http://localhost:24750"
# If application has a context path, set it here, otherwise leave empty
CONTEXT_PATH = "/api"  # Change this if needed
HEADERS = {"Content-Type": "application/json"}


class HealthApiPublicTester:
    """Class for testing the public Health API endpoints"""
    
    def __init__(self, base_url: str = BASE_URL, context_path: str = CONTEXT_PATH):
        self.base_url = base_url
        self.context_path = context_path
        self.success_count = 0
        self.failure_count = 0
        self.clinic_ids = []
        self.doctor_ids = []
    
    def run_all_tests(self) -> None:
        """Execute all test cases and log results"""
        try:
            # Registration tests
            logger.info("=== Starting Registration Tests ===")
            self.test_clinic_registration()
            self.test_customer_registration()
            
            # Search tests
            logger.info("=== Starting Search Tests ===")
            self.test_get_all_clinics()
            self.test_get_all_doctors()
            
            if self.clinic_ids:
                self.test_get_clinic_by_id(self.clinic_ids[0])
                self.test_get_doctors_by_clinic(self.clinic_ids[0])
            
            if self.doctor_ids:
                self.test_get_doctor_by_id(self.doctor_ids[0])
                self.test_get_doctor_timetable(self.doctor_ids[0])
            
            # Try all endpoints without context path if all tests failed
            if self.failure_count > 0 and self.success_count == 0 and self.context_path:
                logger.info("=== All tests failed with context path. Retrying without context path ===")
                self.context_path = ""
                self.run_all_tests()
            
        except Exception as e:
            logger.error(f"Test suite execution failed: {str(e)}")
        finally:
            # Print summary
            total = self.success_count + self.failure_count
            if total > 0:
                logger.info("=== Test Execution Summary ===")
                logger.info(f"Total tests: {total}")
                logger.info(f"Successful: {self.success_count}")
                logger.info(f"Failed: {self.failure_count}")
                logger.info(f"Success rate: {self.success_count/total*100:.2f}%")
            else:
                logger.info("No tests were executed")
    
    def _make_request(
        self, 
        method: str, 
        endpoint: str, 
        data: Optional[Dict] = None,
        expected_status: int = 200
    ) -> Tuple[bool, Any]:
        """
        Make an HTTP request to the API and validate the response
        
        Args:
            method: HTTP method (GET, POST, DELETE, etc.)
            endpoint: API endpoint path
            data: Request payload (will be converted to JSON)
            expected_status: Expected HTTP status code
            
        Returns:
            Tuple of (success_flag, response_data)
        """
        url = f"{self.base_url}{self.context_path}{endpoint}"
        headers = HEADERS.copy()
        
        try:
            logger.info(f"Making {method} request to {url}")
            if method.upper() == "GET":
                response = requests.get(url, headers=headers)
            elif method.upper() == "POST":
                response = requests.post(url, headers=headers, data=json.dumps(data) if data else None)
            else:
                logger.error(f"Unsupported HTTP method: {method}")
                return False, None
            
            # Check if status code matches expected
            if response.status_code != expected_status:
                logger.error(f"Request failed: {endpoint} - Status: {response.status_code}, Expected: {expected_status}")
                logger.error(f"Response: {response.text[:200]}...")  # Print first 200 chars of response
                self.failure_count += 1
                return False, response.text
            
            # Parse response if it has content
            response_data = None
            if response.text:
                try:
                    response_data = response.json()
                except json.JSONDecodeError:
                    response_data = response.text
            
            self.success_count += 1
            return True, response_data
            
        except requests.exceptions.RequestException as e:
            logger.error(f"Request error for {endpoint}: {str(e)}")
            self.failure_count += 1
            return False, None
    
    # Registration tests
    def test_clinic_registration(self) -> None:
        """Test clinic registration endpoint"""
        logger.info("Testing clinic registration")
        
        # Generate unique email to avoid conflicts
        random_suffix = random.randint(1000, 9999)
        email = f"test_clinic_{int(time.time())}_{random_suffix}@example.com"
        
        clinic_data = {
            "name": f"Test Clinic {random_suffix}",
            "description": "This is a test clinic for API testing",
            "email": email,
            "password": "password123"
        }
        
        success, response = self._make_request(
            "POST", 
            "/user/register/clinic", 
            data=clinic_data
        )
        
        if success:
            logger.info("Clinic registration successful")
    
    def test_customer_registration(self) -> None:
        """Test customer registration endpoint"""
        logger.info("Testing customer registration")
        
        # Generate unique email to avoid conflicts
        random_suffix = random.randint(1000, 9999)
        email = f"test_customer_{int(time.time())}_{random_suffix}@example.com"
        
        customer_data = {
            "firstName": f"Test{random_suffix}",
            "lastName": "Customer",
            "age": 30,
            "weight": 75.5,
            "height": 175,
            "gender": "MALE",
            "email": email,
            "password": "password123"
        }
        
        success, response = self._make_request(
            "POST", 
            "/user/register/customer", 
            data=customer_data
        )
        
        if success:
            logger.info("Customer registration successful")


if __name__ == "__main__":
    logger.info("Starting Health API public endpoints test script")
    # Try first with default configuration
    tester = HealthApiPublicTester()
    tester.run_all_tests()
    
    # If the default configuration didn't work, try with empty context path
    if tester.success_count == 0:
        logger.info("Retrying with empty context path")
        tester = HealthApiPublicTester(context_path="")
        tester.run_all_tests()
    logger.info("API testing completed")