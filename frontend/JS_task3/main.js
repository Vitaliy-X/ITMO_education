// 1
const getNewObjWithPrototype = (obj) => {
    return Object.create(obj)
}

// 2
const getEmptyObj = () => {
    return Object.create(null)
}

// 3
const setPrototypeChain = ({ programmer, student, teacher, person }) => {
    Object.setPrototypeOf(programmer, student);
    Object.setPrototypeOf(student, teacher);
    Object.setPrototypeOf(teacher, person);
};

// 4
const getObjWithEnumerableProperty = () => {
    const obj = {}
    Object.defineProperty(obj, "name", {value: "Alex", enumerable: false})
    Object.defineProperty(obj, "age", {value: "18", enumerable: true})
    Object.defineProperty(obj, "work", {value: "empty", enumerable: false})
    return obj;
}

// 5
const getWelcomeObject = (person) => {
    return Object.create(person, {voice: {value: function() {
                return `Hello, my name is ${this.name}. I am ${this.age}.`;
            },
        }
    });
};

// 6
class Singleton {
    constructor(id) {
        if (Singleton.instance) {
            return Singleton.instance;
        }
        this.id = id;
        Singleton.instance = this;
    }
}

// 7
const defineTimes = () => {
    Number.prototype.times = function(callback) {
        for (let i = 1; i <= this; i++) {
            callback(i, this);
        }
    };
};

// 8
const defineUniq = () => {
    Object.defineProperties(Array.prototype, {uniq: {
            get() {
                const seen= new Map();
                return this.filter(item => !seen.has(item) && seen.set(item, true));
            }
        }
    });
};

// 9
const defineUniqSelf = () => {
    Object.defineProperty(Array.prototype, 'uniqSelf', {
        get: function() {
            const uValues = [...new Set(this)];
            this.length = 0;
            this.push(...uValues);
            return this;
        }
    });
};

module.exports = {
    getNewObjWithPrototype,
    getEmptyObj,
    setPrototypeChain,
    getObjWithEnumerableProperty,
    getWelcomeObject,
    Singleton,
    defineTimes,
    defineUniq,
    defineUniqSelf,
}