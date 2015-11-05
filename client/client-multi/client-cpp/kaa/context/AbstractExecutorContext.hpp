/*
 * Copyright 2014-2015 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef ABSTRACTEXECUTORCONTEXT_HPP_
#define ABSTRACTEXECUTORCONTEXT_HPP_

#include <memory>
#include <cstdlib>

#include "kaa/utils/ThreadPool.hpp"
#include "kaa/context/IExecutorContext.hpp"

namespace kaa {

class AbstractExecutorContext : public IExecutorContext {
public:
	AbstractExecutorContext() : awaitTerminationTimeout_(DEFAULT_AWAIT_TERMINATION_TIMEOUT) {}
protected:
    IThreadPoolPtr createExecutor(std::size_t threadCount)
    {
        return std::make_shared<ThreadPool>(threadCount);
    }

    void shutdownExecutor(IThreadPoolPtr threadPool)
    {
        if (threadPool) {
            threadPool->awaitTermination(awaitTerminationTimeout_);
        }
    }

    void setAwaitTerminationTimeout(std::size_t awaitTerminationTimeout)
    {
        awaitTerminationTimeout_ = awaitTerminationTimeout;
    }

    std::size_t getAwaitTerminationTimeout()
    {
        return awaitTerminationTimeout_;
    }

protected:
	static const std::size_t DEFAULT_AWAIT_TERMINATION_TIMEOUT = 5;

    std::size_t awaitTerminationTimeout_/* = 5*/; // in seconds
};

} /* namespace kaa */

#endif /* ABSTRACTEXECUTORCONTEXT_HPP_ */
