const storage = {
    // хранилище кладовки
    store: new Map(),

    // добавляет зелье на указанную полку, метод ничего не возвращает
    add: function (shelveName, potion) {
        const arr = this.store.get(shelveName) || [];
        arr.push(potion);
        this.store.set(shelveName, arr);
    },

    // Возвращает зелье, если оно есть на любой из полок. Зелье убирается из кладовки (с любой из полок, где есть зелье)
    takePotion: function (namePotion) {
        for (const [key, potions] of this.store) {
            const idx = potions.findIndex(potion => potion.name === namePotion);

            if (idx !== -1) {
                const [potion] = potions.splice(idx, 1);
                this.store.set(key, potions);
                return potion;
            }
        }
    },

    // Использует зелье (вызывая у него функцию "use"). Зелье убирается из кладовки (с любой из полок, где есть зелье).
    usePotion: function (namePotion) {
        let potion = this.takePotion(namePotion)
        if (potion !== undefined) {
            potion.use()
        }
    },

    // Возвращает все зелья с полки. Содержимое полки не меняется
    getAllPotionsFromShelve: function (shelveName) {
        return this.store.get(shelveName)
    },

    // Возвращает все зелья кладовки. Содержимое полок не меняется
    getAllPotions: function () {
        let result = []
        for (const values of this.store.values()) {
            result = [...result, ...values]
        }
        return result
    },

    // Возвращает все зелья с полки. Полка остается пустой
    takeAllPotionsFromShelve: function (shelveName) {
        if (this.store.has(shelveName)) {
            const tmp = this.store.get(shelveName)
            this.store.set(shelveName, [])
            return tmp
        }
    },

    // Использует все зелья с указанной полки. Полка остается пустой
    useAllPotionsFromShelve: function (shelveName) {
        if (this.store.has(shelveName)) {
            const potions = this.takeAllPotionsFromShelve(shelveName)
            for (const potion of potions) {
                potion.use()
            }
        }
    },

    // Возвращает зелья с истекшим сроком хранения. Метод убирает такие зелья из кладовки.
    // revisionDay - день (Date), в который происходит проверка сроков хранения
    clean: function(revisionDay) {
        const potions = this.getAllPotions();
        let expired = [];

        for (const potion of potions) {
            const expirationDate = new Date(potion.created);
            expirationDate.setDate(expirationDate.getDate() + potion.expirationDays);

            if (expirationDate < revisionDay) {
                this.takePotion(potion.name);
                expired.push(potion);
            }
        }
        return expired;
    },

    // возвращает число - сколько уникальных названий зелий находится в кладовке
    uniquePotionsCount: function() {
        const unique = new Set();
        for (const potions of this.store.values()) {
            for (const potion of potions) {
                unique.add(potion.name);
            }
        }
        return unique.size;
    },
}

function makePotionsRoom() {
    return storage
}


// Можно добавлять зелья на полку

const potionsRoom = makePotionsRoom();

const potion1 = { name: 'Зелье №1'};
const potion2 = { name: 'Зелье №2'};
const potion3 = { name: 'Зелье №3'};
const potion4 = { name: 'Зелье №4'};

potionsRoom.add('Полка №1', potion1);
potionsRoom.add('Полка №1', potion2);
potionsRoom.add('Полка №1', potion3);
potionsRoom.add('Полка №2', potion4);

potionsRoom.takePotion('Зелье №1');

potionsRoom.clean()

console.log(potionsRoom)