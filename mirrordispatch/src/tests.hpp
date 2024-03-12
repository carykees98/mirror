#pragma once

namespace mirror {
    /**
     * Tests for MirrorDispatch 
     */
    namespace Tests {
        /**
         * Runs all tests.
         */
        int run();

        /**
         * Tests class Status
         */
        bool testStatus();

        /**
         * Tests class Config 
         */
        bool testConfig();
    }
}
