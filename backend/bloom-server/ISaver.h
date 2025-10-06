#ifndef ISAVER_H
#define ISAVER_H

#include <string>

template<typename T>
// Interface for saving data
class ISaver {
public:
    virtual ~ISaver() = default;
    virtual void save(const T& data) = 0;
};

#endif
