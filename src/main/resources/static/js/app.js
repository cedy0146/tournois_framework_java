(function () {
    'use strict';

    /* Intervalle du diaporama du hero (ms) — à ajuster ici */
    const HERO_SLIDE_DELAY = 3500;

    /* ---------------- Thème sombre / clair ---------------- */
    const root = document.documentElement;
    const toggle = document.querySelector('[data-theme-toggle]');
    const icon = document.querySelector('[data-theme-icon]');

    function updateThemeIcon() {
        if (!icon) return;
        const dark = root.dataset.theme === 'dark';
        icon.className = dark ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
        toggle?.setAttribute('aria-label', dark ? 'Activer le mode clair' : 'Activer le mode sombre');
    }

    updateThemeIcon();
    toggle?.addEventListener('click', function () {
        root.dataset.theme = root.dataset.theme === 'dark' ? 'light' : 'dark';
        localStorage.setItem('tournoi-theme', root.dataset.theme);
        updateThemeIcon();
    });

    document.querySelectorAll('.reveal').forEach(function (element, index) {
        element.style.animationDelay = Math.min(index * 45, 360) + 'ms';
    });

    /* ---------------- Diaporama des heroes (fondu enchaîné) ---------------- */
    (function heroSlideshow() {
        const motionQuery = window.matchMedia('(prefers-reduced-motion: reduce)');

        document.querySelectorAll('.hero-slides').forEach(function (slidesEl) {
            const slides = Array.prototype.slice.call(slidesEl.querySelectorAll('.hero-slide'));
            if (slides.length < 2) return;

            const hero = slidesEl.closest('.page-hero') || slidesEl.parentElement;
            if (!hero) return;

            // Préchargement : évite un flash blanc au premier fondu
            slides.forEach(function (slide) {
                const match = /url\(["']?(.*?)["']?\)/.exec(slide.style.backgroundImage || '');
                if (match && match[1]) {
                    const img = new Image();
                    img.src = match[1];
                }
            });

            let index = slides.findIndex(function (slide) { return slide.classList.contains('is-active'); });
            if (index < 0) {
                index = 0;
                slides[0].classList.add('is-active');
            }

            let timer = null;
            // Si le pointeur est déjà sur le hero au chargement, on part en pause
            let hovering = hero.matches(':hover');

            const dotsWrap = document.createElement('div');
            dotsWrap.className = 'hero-dots';
            dotsWrap.setAttribute('role', 'group');
            dotsWrap.setAttribute('aria-label', 'Choisir une image de fond');
            const dots = slides.map(function (_slide, i) {
                const dot = document.createElement('button');
                dot.type = 'button';
                dot.className = 'hero-dot' + (i === index ? ' is-active' : '');
                dot.setAttribute('aria-label', 'Image ' + (i + 1) + ' sur ' + slides.length);
                dot.addEventListener('click', function () {
                    show(i);
                    restart();
                });
                dotsWrap.appendChild(dot);
                return dot;
            });
            hero.appendChild(dotsWrap);

            function show(next) {
                if (next === index || next < 0 || next >= slides.length) return;
                slides[index].classList.remove('is-active');
                index = next;
                slides[index].classList.add('is-active');
                dots.forEach(function (dot, i) { dot.classList.toggle('is-active', i === index); });
            }

            function isPaused() {
                return hovering || document.hidden || motionQuery.matches;
            }

            function tick() {
                if (!isPaused()) show((index + 1) % slides.length);
            }

            function start() {
                if (!timer && !isPaused()) timer = window.setInterval(tick, HERO_SLIDE_DELAY);
            }

            function stop() {
                if (timer) {
                    window.clearInterval(timer);
                    timer = null;
                }
            }

            function restart() {
                stop();
                start();
            }

            hero.addEventListener('mouseenter', function () { hovering = true; stop(); });
            hero.addEventListener('mouseleave', function () { hovering = false; start(); });

            document.addEventListener('visibilitychange', function () {
                if (document.hidden) stop(); else start();
            });

            if (typeof motionQuery.addEventListener === 'function') {
                motionQuery.addEventListener('change', restart);
            } else if (typeof motionQuery.addListener === 'function') {
                motionQuery.addListener(restart);
            }

            start();
        });
    })();

    /* ---------------- Modale applicative ---------------- */
    const modalEl = document.getElementById('appModal');
    const contentEl = document.getElementById('appModalContent');
    if (!modalEl || !contentEl) return;

    const dialogEl = modalEl.querySelector('.modal-dialog');
    const XHR_HEADERS = { 'X-Requested-With': 'XMLHttpRequest' };
    let bsModal = null;
    let lastTrigger = null;
    let lastModalUrl = null;
    let modalHidden = true;
    let openPending = false;

    const FLASH_KEY = 'appFlash';
    const FLASH_TYPE_KEY = 'appFlashType';

    (function consumeFlash() {
        try {
            const message = sessionStorage.getItem(FLASH_KEY);
            if (!message) return;
            const type = sessionStorage.getItem(FLASH_TYPE_KEY) || 'success';
            sessionStorage.removeItem(FLASH_KEY);
            sessionStorage.removeItem(FLASH_TYPE_KEY);
            window.setTimeout(function () { showToast(message, type); }, 250);
        } catch (err) {
            /* stockage indisponible */
        }
    })();

    function flashAndReload(message) {
        try {
            sessionStorage.setItem(FLASH_KEY, message);
            sessionStorage.setItem(FLASH_TYPE_KEY, 'success');
        } catch (err) {
            /* stockage indisponible : on affiche avant le rechargement */
            showToast(message, 'success');
        }
        closeModal();
        window.setTimeout(function () { window.location.reload(); }, 350);
    }

    function modalInstance() {
        if (!bsModal) bsModal = bootstrap.Modal.getOrCreateInstance(modalEl);
        return bsModal;
    }

    function setModalSize(size) {
        if (!dialogEl) return;
        dialogEl.classList.remove('modal-lg', 'modal-md');
        dialogEl.classList.add(size === 'md' ? 'modal-md' : 'modal-lg');
    }

    function isModalOpen() {
        return modalEl.classList.contains('show');
    }

    function isModalStable() {
        return isModalOpen() && !modalHidden;
    }

    function openModal() {
        if (isModalStable()) return;
        if (!modalHidden) {
            openPending = true;
            return;
        }
        modalInstance().show();
    }

    function closeModal() {
        if (isModalOpen()) modalInstance().hide();
    }

    function setContent(html) {
        contentEl.innerHTML = html;
    }

    function focusFirstField() {
        const target = contentEl.querySelector('[data-autofocus]')
            || contentEl.querySelector('.is-invalid')
            || contentEl.querySelector('input:not([type="hidden"]):not([disabled]), select:not([disabled]), textarea:not([disabled])')
            || contentEl.querySelector('.modal-body a[href], .modal-body button:not(.btn-close)')
            || contentEl.querySelector('.modal-footer button');
        if (target) target.focus({ preventScroll: true });
    }

    function showToast(message, type) {
        const toastEl = document.getElementById('appToast');
        const bodyEl = document.getElementById('appToastBody');
        const titleEl = document.getElementById('appToastTitle');
        if (!toastEl || !bodyEl) {
            window.alert(message);
            return;
        }
        bodyEl.textContent = message;
        const danger = type === 'danger';
        if (titleEl) titleEl.textContent = danger ? 'Erreur' : 'Succès';
        const headerIcon = toastEl.querySelector('.toast-header i');
        if (headerIcon) {
            headerIcon.className = (danger ? 'bi bi-exclamation-triangle-fill text-danger' : 'bi bi-check-circle-fill text-success') + ' me-2';
        }
        bootstrap.Toast.getOrCreateInstance(toastEl, { delay: 3800 }).show();
    }

    function readCsrfToken() {
        const meta = document.querySelector('meta[name="_csrf"]');
        if (meta && meta.content) return meta.content;
        const input = document.querySelector('input[name="_csrf"]');
        return input ? input.value : null;
    }

    async function loadIntoModal(url, trigger, size) {
        if (!isModalStable()) lastTrigger = trigger || document.activeElement;
        setModalSize(size || 'lg');
        const res = await fetch(url, {
            headers: XHR_HEADERS,
            credentials: 'same-origin'
        });
        const html = await res.text();
        if (!res.ok && html.indexOf('modal-header') === -1) {
            throw new Error('HTTP ' + res.status);
        }
        setContent(html);
        lastModalUrl = url;
        openModal();
        focusFirstField();
    }

    async function handlePostResponse(res, opts) {
        opts = opts || {};
        const contentType = (res.headers.get('content-type') || '').toLowerCase();

        // Redirection suivie = action exécutée avec succès
        if (res.redirected) {
            if (opts.stayOpen) {
                const refreshUrl = opts.refreshUrl || lastModalUrl;
                if (refreshUrl) {
                    const fresh = await fetch(refreshUrl, { headers: XHR_HEADERS, credentials: 'same-origin' });
                    if (fresh.ok) {
                        const freshHtml = await fresh.text();
                        if (freshHtml && freshHtml.trim()) {
                            setContent(freshHtml);
                            focusFirstField();
                        }
                    }
                }
                if (opts.message) showToast(opts.message, 'success');
                return;
            }
            flashAndReload(opts.message || 'Enregistré avec succès');
            return;
        }

        if (contentType.indexOf('application/json') !== -1) {
            const data = await res.json().catch(function () { return {}; });
            showToast(data.message || data.error || 'Une erreur est survenue', 'danger');
            return;
        }

        const html = await res.text();
        if (html && html.trim()) {
            setContent(html);
            focusFirstField();
        } else if (!res.ok) {
            showToast('Une erreur est survenue (' + res.status + ')', 'danger');
        } else if (opts.message) {
            flashAndReload(opts.message);
        }
    }

    /* --- Chargement d'une page dans la modale (liens data-modal-url) --- */
    document.addEventListener('click', function (event) {
        const link = event.target.closest('[data-modal-url]');
        if (link) {
            if (event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
            event.preventDefault();
            const url = link.getAttribute('data-modal-url') || link.getAttribute('href');
            const size = link.getAttribute('data-modal-size') || 'lg';
            loadIntoModal(url, link, size).catch(function (err) {
                console.error(err);
                showToast('Impossible de charger le contenu', 'danger');
            });
            return;
        }

        const confirmBtn = event.target.closest('[data-modal-confirm]');
        if (confirmBtn) {
            if (event.defaultPrevented || event.button !== 0) return;
            event.preventDefault();
            openConfirmDialog(confirmBtn);
        }
    }, false);

    /* --- Modale de confirmation de suppression --- */
    function openConfirmDialog(trigger) {
        const form = trigger.closest('form');
        const url = trigger.getAttribute('data-modal-url') ||
            (form && form.getAttribute('action')) ||
            trigger.getAttribute('href');
        const name = trigger.getAttribute('data-entity-name') || 'cet élément';
        if (!isModalStable()) lastTrigger = trigger;
        setModalSize('md');
        setContent(
            '<div class="modal-header">' +
                '<h2 class="modal-title fs-5" id="appModalTitle">Confirmer la suppression</h2>' +
                '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer"></button>' +
            '</div>' +
            '<div class="modal-body">' +
                '<p class="mb-0">Voulez-vous vraiment supprimer <strong id="appConfirmName"></strong>&nbsp;?</p>' +
            '</div>' +
            '<div class="modal-footer">' +
                '<button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Annuler</button>' +
                '<button type="button" class="btn btn-danger" id="appConfirmDelete">Supprimer</button>' +
            '</div>'
        );
        const nameEl = contentEl.querySelector('#appConfirmName');
        if (nameEl) nameEl.textContent = name;
        const confirmBtn = contentEl.querySelector('#appConfirmDelete');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', function () {
                submitDelete(url, confirmBtn);
            });
        }
        openModal();
    }

    async function submitDelete(url, button) {
        if (button) button.disabled = true;
        try {
            const fd = new FormData();
            const csrf = readCsrfToken();
            if (csrf) fd.append('_csrf', csrf);
            const res = await fetch(url, {
                method: 'POST',
                body: fd,
                headers: XHR_HEADERS,
                credentials: 'same-origin'
            });
            await handlePostResponse(res, { message: 'Supprimé avec succès' });
        } catch (err) {
            console.error(err);
            showToast('Erreur réseau lors de la suppression', 'danger');
        } finally {
            if (button) button.disabled = false;
        }
    }

    /* --- Soumission des formulaires de la modale --- */
    contentEl.addEventListener('submit', function (event) {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)) return;
        if (!form.hasAttribute('data-modal-form') && !form.hasAttribute('data-modal-action')) return;
        event.preventDefault();
        submitModalForm(form);
    });

    async function submitModalForm(form) {
        const isAction = form.hasAttribute('data-modal-action');
        const submitBtn = form.querySelector('[type="submit"]');
        const originalHtml = submitBtn ? submitBtn.innerHTML : null;
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1" aria-hidden="true"></span>…';
        }
        try {
            const res = await fetch(form.getAttribute('action'), {
                method: (form.getAttribute('method') || 'post').toUpperCase(),
                body: new FormData(form),
                headers: XHR_HEADERS,
                credentials: 'same-origin'
            });
            await handlePostResponse(res, {
                message: form.getAttribute('data-success-message') || 'Enregistré avec succès',
                stayOpen: isAction,
                refreshUrl: isAction ? form.getAttribute('data-modal-refresh-url') || null : null
            });
        } catch (err) {
            console.error(err);
            showToast('Erreur réseau', 'danger');
        } finally {
            if (submitBtn && document.contains(submitBtn)) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalHtml;
            }
        }
    }

    /* --- Accessibilité : focus + retour au déclencheur --- */
    modalEl.addEventListener('hide.bs.modal', function () {
        modalHidden = false;
        openPending = false;
    });

    modalEl.addEventListener('shown.bs.modal', function () {
        focusFirstField();
    });

    modalEl.addEventListener('hidden.bs.modal', function () {
        modalHidden = true;
        if (openPending) {
            openPending = false;
            modalInstance().show();
            return;
        }
        contentEl.innerHTML = '';
        if (lastTrigger && document.contains(lastTrigger)) {
            lastTrigger.focus({ preventScroll: true });
        }
        lastTrigger = null;
    });
})();

/* ---------------- Modale de connexion ---------------- */
(function () {
    'use strict';

    var loginEl = document.getElementById('loginModal');
    if (!loginEl || typeof bootstrap === 'undefined' || !bootstrap.Modal) return;

    var userField = document.getElementById('loginUsername');
    var passwordField = document.getElementById('loginPassword');
    var authMeta = document.querySelector('meta[name="authenticated"]');
    // 'true' / 'false' selon le rendu serveur ; null si la balise est absente (on laisse alors la navigation normale)
    var authState = authMeta && authMeta.content ? authMeta.content : null;
    var bsLogin = null;

    var loginForm = loginEl.querySelector('[data-login-form]');
    var submitBtn = loginEl.querySelector('[data-login-submit]');
    var capsWarning = loginEl.querySelector('[data-caps-warning]');
    var panel = loginEl.querySelector('[data-login-panel]');

    function loginModal() {
        if (!bsLogin) bsLogin = bootstrap.Modal.getOrCreateInstance(loginEl);
        return bsLogin;
    }

    /* --- Supprimer les bandeaux (erreur / déconnexion) --- */
    function removeAlerts() {
        loginEl.querySelectorAll('.lp-login-alert').forEach(function (el) { el.remove(); });
    }

    /* --- Réinitialiser le bouton de soumission --- */
    function resetSubmit() {
        if (!submitBtn) return;
        submitBtn.disabled = false;
        submitBtn.classList.remove('is-loading');
    }

    /* --- Afficher / masquer le mot de passe --- */
    document.querySelectorAll('[data-password-toggle]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var input = btn.parentElement ? btn.parentElement.querySelector('input') : null;
            if (!input) return;
            var reveal = input.type === 'password';
            input.type = reveal ? 'text' : 'password';
            btn.setAttribute('aria-label', reveal ? 'Masquer le mot de passe' : 'Afficher le mot de passe');
            var icon = btn.querySelector('i');
            if (icon) icon.className = reveal ? 'bi bi-eye-slash' : 'bi bi-eye';
            input.focus({ preventScroll: true });
        });
    });

    /* --- CapsLock sur le champ mot de passe --- */
    if (capsWarning && passwordField) {
        ['keydown', 'keyup'].forEach(function (type) {
            passwordField.addEventListener(type, function (event) {
                var on = typeof event.getModifierState === 'function' && event.getModifierState('CapsLock');
                capsWarning.hidden = !on;
            });
        });
        passwordField.addEventListener('blur', function () { capsWarning.hidden = true; });
    }

    /* --- Validation HTML5 : bordure + message "Champ requis" --- */
    if (loginForm) {
        loginForm.addEventListener('invalid', function () {
            loginForm.classList.add('was-validated');
        }, true);

        /* Dès que l'utilisateur tape, on masque le bandeau d'erreur */
        loginForm.addEventListener('input', removeAlerts);

        /* Anti double-envoi : état chargement au submit */
        loginForm.addEventListener('submit', function () {
            if (!submitBtn || submitBtn.disabled) return;
            submitBtn.disabled = true;
            submitBtn.classList.add('is-loading');
        });
    }

    /* --- Boutons réservés aux connectés : modal si anonyme --- */
    document.addEventListener('click', function (event) {
        if (authState !== 'false') return;
        var trigger = event.target.closest('[data-login-required]');
        if (!trigger) return;
        if (event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
        event.preventDefault();
        loginModal().show();
    });

    /* --- À chaque ouverture : focus identifiant + secousse du modal sur erreur --- */
    loginEl.addEventListener('shown.bs.modal', function () {
        var errorAlert = loginEl.querySelector('.lp-login-alert--error');
        if (errorAlert) {
            errorAlert.style.animation = 'none';
            void errorAlert.offsetHeight; // reflow : relance l'animation d'entrée
            errorAlert.style.animation = '';
        }
        if (errorAlert && panel) {
            panel.classList.remove('lp-login--shake');
            void panel.offsetWidth; // reflow : relance la secousse
            panel.classList.add('lp-login--shake');
        }
        if (userField) userField.focus({ preventScroll: true });
    });

    /* --- À la fermeture : on repart d'un état propre --- */
    loginEl.addEventListener('hidden.bs.modal', function () {
        removeAlerts();
        resetSubmit();
        if (loginForm) loginForm.classList.remove('was-validated');
        if (capsWarning) capsWarning.hidden = true;
        if (panel) panel.classList.remove('lp-login--shake');
    });

    /* --- Ouverture auto selon l'URL (?login / ?error / ?logout), puis nettoyage --- */
    (function autoOpenFromUrl() {
        var params = new URLSearchParams(window.location.search);
        var flags = ['login', 'error', 'logout'];
        var matched = flags.some(function (flag) { return params.has(flag); });
        if (!matched) return;

        flags.forEach(function (flag) { params.delete(flag); });
        var query = params.toString();
        try {
            history.replaceState(null, '', window.location.pathname + (query ? '?' + query : '') + window.location.hash);
        } catch (err) {
            /* historique indisponible : on ouvre quand même */
        }
        window.setTimeout(function () { loginModal().show(); }, 120);
    })();

    /* Échap = fermeture : géré nativement par Bootstrap (data-bs-keyboard="true"). */
})();

/* ---------------- Révélation des sections au scroll ---------------- */
(function () {
    'use strict';

    var targets = document.querySelectorAll('.home-reveal');
    if (!targets.length) return;

    // Repli : sans IntersectionObserver (ou si mouvement réduit), tout est visible immédiatement
    var reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (!('IntersectionObserver' in window) || reducedMotion) {
        targets.forEach(function (el) { el.classList.add('is-visible'); });
        return;
    }

    var observer = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (!entry.isIntersecting) return;
            entry.target.classList.add('is-visible');
            observer.unobserve(entry.target);
        });
    }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });

    targets.forEach(function (el) { observer.observe(el); });
})();

/* ---------------- Gestion de la sidebar flottante ---------------- */
(function () {
    'use strict';
    try {
        const appShell = document.getElementById('appShell');
        const appSidebar = document.getElementById('appSidebar');
        const sidebarToggle = document.getElementById('sidebarToggle');
        const mobileBurger = document.getElementById('mobileBurger');
        const sidebarBackdrop = document.getElementById('sidebarBackdrop');
        const tournamentsGroup = document.getElementById('tournamentsGroup');
        if (!appShell || !appSidebar) return;
        const STORAGE_KEY = 'tournoi-sidebar';
        if (window.innerWidth >= 992) {
            try {
                if (localStorage.getItem(STORAGE_KEY) === 'true') {
                    appShell.classList.add('is-collapsed');
                }
            } catch (e) {}
        }
        if (sidebarToggle) {
            sidebarToggle.addEventListener('click', function () {
                try {
                    const collapsed = appShell.classList.toggle('is-collapsed');
                    localStorage.setItem(STORAGE_KEY, collapsed ? 'true' : 'false');
                    if (tournamentsGroup) tournamentsGroup.classList.remove('flyout-open');
                } catch (e) {}
            });
        }
        if (tournamentsGroup) {
            const parentBtn = tournamentsGroup.querySelector('.sidebar-parent-btn');
            const closeFlyout = function () {
                if (tournamentsGroup.classList.contains('flyout-open')) {
                    tournamentsGroup.classList.remove('flyout-open');
                    if (parentBtn) parentBtn.setAttribute('aria-expanded', 'false');
                }
            };
            if (parentBtn) {
                parentBtn.addEventListener('click', function (e) {
                    e.preventDefault();
                    if (appShell.classList.contains('is-collapsed') && window.innerWidth >= 992) {
                        const flyoutOpen = tournamentsGroup.classList.toggle('flyout-open');
                        parentBtn.setAttribute('aria-expanded', flyoutOpen ? 'true' : 'false');
                    } else {
                        const isOpen = tournamentsGroup.classList.toggle('is-open');
                        parentBtn.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
                    }
                });
            }
            tournamentsGroup.addEventListener('mouseleave', function () {
                if (appShell.classList.contains('is-collapsed')) {
                    closeFlyout();
                }
            });
            window.addEventListener('blur', closeFlyout);
            document.addEventListener('click', function (e) {
                if (appShell.classList.contains('is-collapsed') &&
                    tournamentsGroup.classList.contains('flyout-open') &&
                    !tournamentsGroup.contains(e.target)) {
                    closeFlyout();
                }
            });
        }
        function openDrawer() {
            try {
                appSidebar.classList.add('is-drawer-open');
                if (sidebarBackdrop) sidebarBackdrop.classList.add('is-visible');
                document.body.style.overflow = 'hidden';
            } catch (e) {}
        }
        function closeDrawer() {
            try {
                appSidebar.classList.remove('is-drawer-open');
                if (sidebarBackdrop) sidebarBackdrop.classList.remove('is-visible');
                document.body.style.overflow = '';
            } catch (e) {}
        }
        if (mobileBurger) {
            mobileBurger.addEventListener('click', function () { openDrawer(); });
        }
        if (sidebarBackdrop) {
            sidebarBackdrop.addEventListener('click', function () { closeDrawer(); });
        }
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                if (tournamentsGroup && tournamentsGroup.classList.contains('flyout-open')) {
                    tournamentsGroup.classList.remove('flyout-open');
                    const btn = tournamentsGroup.querySelector('.sidebar-parent-btn');
                    if (btn) btn.setAttribute('aria-expanded', 'false');
                }
                if (appSidebar.classList.contains('is-drawer-open')) {
                    closeDrawer();
                }
            }
        });
        window.addEventListener('resize', function () {
            if (window.innerWidth >= 992) closeDrawer();
        });
    } catch (err) {
        try { console.error('Erreur initialisation sidebar:', err); } catch (e) {}
    }
})();
/* ---------------- Révélation des sections (landing page) ---------------- */
(function () {
    'use strict';

    var targets = document.querySelectorAll('.lp-reveal');
    if (!targets.length) return;

    // Repli : sans IntersectionObserver (ou si mouvement réduit), tout est visible immédiatement
    var reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (!('IntersectionObserver' in window) || reducedMotion) {
        targets.forEach(function (el) { el.classList.add('is-visible'); });
        return;
    }

    var observer = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (!entry.isIntersecting) return;
            entry.target.classList.add('is-visible');
            observer.unobserve(entry.target);
        });
    }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });

    targets.forEach(function (el) { observer.observe(el); });
})();

