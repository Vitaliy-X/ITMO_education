'use strict';

document.addEventListener('DOMContentLoaded', () => {
    const menuButtons = document.querySelectorAll('.selection-button');
    const productButtons = document.querySelectorAll('.product-button');
    const menuState = new Map();
    const productState = new Map();

    function toggleMenu(menuButton, dropdownMenu, label) {
        const isActive = menuState.get(menuButton.id);

        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.classList.remove('active');
        });
        document.querySelectorAll('.menu-item').forEach(item => {
            item.classList.remove('active');
        });
        menuButtons.forEach(button => {
            menuState.set(button.id, false);
            button.classList.remove('active');
        });

        if (!isActive) {
            dropdownMenu.classList.add('active');
            label.classList.add('active');
            menuState.set(menuButton.id, true);
            menuButton.classList.add('active');
        }
    }

    function toggleProduct(productButton, currentContentList) {
        productLists.forEach(list => list.classList.remove('active'));
        currentContentList.classList.add('active');

        productButtons.forEach(button => {
            productState.set(button.id, false);
        });
        productState.set(productButton.id, true);
    }

    menuButtons.forEach(menuButton => {
        const dropdownMenu = document.querySelector(`.dropdown-menu[data-menu="${menuButton.id}"]`);
        const label = document.querySelector(`label[for="${menuButton.id}"]`);
        menuState.set(menuButton.id, menuButton.checked);

        menuButton.addEventListener('click', () => {
            toggleMenu(menuButton, dropdownMenu, label);
        });
    });

    const productLists = document.querySelectorAll(".mws-product-list");

    productButtons.forEach(productButton => {
        const currentContentList = document.querySelector(`.mws-${productButton.id}-list`);
        productState.set(productButton.id, productButton.checked);

        if (productButton.checked) {
            currentContentList.classList.add('active');
        }

        productButton.addEventListener('click', () => {
            toggleProduct(productButton, currentContentList);
        });
    });
});
