(function () {
  'use strict';

  var API = '/api';

  // ---------- Generic helpers ----------

  function qs(id) { return document.getElementById(id); }

  function showToast(message, isError) {
    var el = qs('toast');
    el.textContent = message;
    el.className = 'toast' + (isError ? ' error' : '');
    el.hidden = false;
    clearTimeout(showToast._t);
    showToast._t = setTimeout(function () { el.hidden = true; }, 3500);
  }

  function escapeHtml(str) {
    return String(str).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  // request() returns {ok, status, body} and never throws for HTTP-level errors,
  // only for network failures.
  function request(method, path, body) {
    var opts = { method: method, headers: {} };
    if (body !== undefined) {
      opts.headers['Content-Type'] = 'application/json';
      opts.body = JSON.stringify(body);
    }
    return fetch(API + path, opts).then(function (res) {
      if (res.status === 204) return { ok: true, status: 204, body: null };
      return res.json().catch(function () { return null; }).then(function (json) {
        return { ok: res.ok, status: res.status, body: json };
      });
    });
  }

  function fieldErrors(errorBody) {
    // ErrorResponse.details is a list like ["name: name is required", "email: must be valid"]
    var map = {};
    if (errorBody && Array.isArray(errorBody.details)) {
      errorBody.details.forEach(function (line) {
        var idx = line.indexOf(':');
        if (idx > -1) {
          map[line.slice(0, idx).trim()] = line.slice(idx + 1).trim();
        }
      });
    }
    return map;
  }

  // ---------- Tabs ----------

  document.querySelectorAll('.tab').forEach(function (tab) {
    tab.addEventListener('click', function () {
      document.querySelectorAll('.tab').forEach(function (t) { t.classList.remove('active'); });
      document.querySelectorAll('.panel').forEach(function (p) { p.classList.remove('active'); });
      tab.classList.add('active');
      qs('panel-' + tab.dataset.tab).classList.add('active');
      if (tab.dataset.tab === 'enrollments') loadDropdowns();
    });
  });

  // ---------- Students ----------

  var studentsState = { page: 0, size: 8, searchName: null, sortField: null, sortDir: 'asc' };

  var STATUS_TRANSITIONS = {
    ACTIVE: ['ACTIVE', 'INACTIVE', 'GRADUATED'],
    INACTIVE: ['INACTIVE', 'ACTIVE', 'GRADUATED'],
    GRADUATED: ['GRADUATED']
  };

  function statusBadge(status) {
    var cls = status === 'ACTIVE' ? 'badge-active' : status === 'GRADUATED' ? 'badge-graduated' : 'badge-inactive';
    return '<span class="badge ' + cls + '">' + status + '</span>';
  }

  function statusSelect(id, status) {
    var options = (STATUS_TRANSITIONS[status] || [status]).map(function (s) {
      return '<option value="' + s + '"' + (s === status ? ' selected' : '') + '>' + s + '</option>';
    }).join('');
    var disabled = status === 'GRADUATED' ? ' disabled title="GRADUATED is terminal"' : '';
    return '<select class="status-select" data-id="' + id + '" data-current="' + status + '"' + disabled + '>' + options + '</select>';
  }

  function loadStudents() {
    var path = studentsState.searchName
      ? '/students/search?name=' + encodeURIComponent(studentsState.searchName) + '&page=' + studentsState.page + '&size=' + studentsState.size
      : '/students?page=' + studentsState.page + '&size=' + studentsState.size;
    if (studentsState.sortField) {
      path += '&sort=' + studentsState.sortField + ',' + studentsState.sortDir;
    }

    request('GET', path).then(function (res) {
      var body = qs('students-body');
      if (!res.ok) {
        body.innerHTML = '<tr><td colspan="5" class="empty">Failed to load students.</td></tr>';
        return;
      }
      var page = res.body;
      if (!page.content.length) {
        body.innerHTML = '<tr><td colspan="5" class="empty">No students found.</td></tr>';
      } else {
        body.innerHTML = page.content.map(function (s) {
          return '<tr>' +
            '<td>' + s.id + '</td>' +
            '<td>' + escapeHtml(s.name) + '</td>' +
            '<td>' + escapeHtml(s.email) + '</td>' +
            '<td>' + statusBadge(s.status) + ' ' + statusSelect(s.id, s.status) + '</td>' +
            '<td class="row-actions">' +
              '<button data-action="edit" data-id="' + s.id + '">Edit</button>' +
              '<button data-action="delete" class="delete" data-id="' + s.id + '" data-name="' + escapeHtml(s.name) + '">Delete</button>' +
            '</td>' +
          '</tr>';
        }).join('');
      }
      qs('students-page-info').textContent = 'Page ' + (page.number + 1) + ' of ' + Math.max(page.totalPages, 1) + ' (' + page.totalElements + ' total)';
      qs('students-prev').disabled = page.first;
      qs('students-next').disabled = page.last;
    });
  }

  qs('students-body').addEventListener('change', function (e) {
    var select = e.target.closest('select.status-select');
    if (!select) return;
    var id = select.dataset.id;
    var newStatus = select.value;
    if (newStatus === select.dataset.current) return;
    request('PATCH', '/students/' + id + '/status', { status: newStatus }).then(function (res) {
      if (res.ok) {
        showToast('Status updated to ' + newStatus + '.');
      } else {
        showToast(res.body && res.body.message ? res.body.message : 'Status change failed.', true);
      }
      loadStudents();
    });
  });

  qs('students-prev').addEventListener('click', function () {
    if (studentsState.page > 0) { studentsState.page--; loadStudents(); }
  });
  qs('students-next').addEventListener('click', function () {
    studentsState.page++; loadStudents();
  });

  function updateSortIndicators() {
    document.querySelectorAll('#students-head-row th.sortable').forEach(function (th) {
      var indicator = th.querySelector('.sort-indicator');
      if (th.dataset.sort === studentsState.sortField) {
        th.classList.add('sorted');
        indicator.textContent = studentsState.sortDir === 'asc' ? '▲' : '▼';
      } else {
        th.classList.remove('sorted');
        indicator.textContent = '';
      }
    });
  }

  qs('students-head-row').addEventListener('click', function (e) {
    var th = e.target.closest('th.sortable');
    if (!th) return;
    var field = th.dataset.sort;
    if (studentsState.sortField === field) {
      studentsState.sortDir = studentsState.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      studentsState.sortField = field;
      studentsState.sortDir = 'asc';
    }
    studentsState.page = 0;
    updateSortIndicators();
    loadStudents();
  });

  qs('btn-search-students').addEventListener('click', function () {
    var v = qs('student-search').value.trim();
    studentsState.searchName = v || null;
    studentsState.page = 0;
    loadStudents();
  });
  qs('student-search').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') qs('btn-search-students').click();
  });
  qs('btn-clear-search').addEventListener('click', function () {
    qs('student-search').value = '';
    studentsState.searchName = null;
    studentsState.page = 0;
    loadStudents();
  });

  qs('students-body').addEventListener('click', function (e) {
    var btn = e.target.closest('button[data-action]');
    if (!btn) return;
    var id = btn.dataset.id;
    if (btn.dataset.action === 'edit') {
      openStudentModal(id);
    } else if (btn.dataset.action === 'delete') {
      openConfirmDelete('student', id, 'This will permanently remove "' + btn.dataset.name + '" (id ' + id + ').');
    }
  });

  // Student modal (create/edit)

  function openStudentModal(id) {
    var backdrop = qs('student-modal-backdrop');
    qs('student-form').reset();
    qs('student-name-error').textContent = '';
    qs('student-email-error').textContent = '';
    qs('student-id').value = id || '';
    qs('student-modal-title').textContent = id ? 'Edit Student' : 'Add Student';

    if (id) {
      request('GET', '/students/' + id).then(function (res) {
        if (res.ok) {
          qs('student-name').value = res.body.name;
          qs('student-email').value = res.body.email;
        }
      });
    }
    backdrop.hidden = false;
  }

  qs('btn-new-student').addEventListener('click', function () { openStudentModal(null); });
  qs('btn-cancel-student').addEventListener('click', function () { qs('student-modal-backdrop').hidden = true; });

  qs('student-form').addEventListener('submit', function (e) {
    e.preventDefault();
    qs('student-name-error').textContent = '';
    qs('student-email-error').textContent = '';

    var id = qs('student-id').value;
    var payload = { name: qs('student-name').value, email: qs('student-email').value };
    var method = id ? 'PUT' : 'POST';
    var path = id ? '/students/' + id : '/students';

    request(method, path, payload).then(function (res) {
      if (res.ok) {
        qs('student-modal-backdrop').hidden = true;
        showToast(id ? 'Student updated.' : 'Student created.');
        loadStudents();
      } else if (res.status === 400) {
        var errs = fieldErrors(res.body);
        if (errs.name) qs('student-name-error').textContent = errs.name;
        if (errs.email) qs('student-email-error').textContent = errs.email;
      } else if (res.status === 409) {
        qs('student-email-error').textContent = res.body.message;
      } else {
        showToast(res.body && res.body.message ? res.body.message : 'Something went wrong.', true);
      }
    });
  });

  // Delete confirmation - shared by students, courses, and enrollments.
  // Each type just needs a DELETE path, a modal title, and a reload callback.

  var DELETE_TYPES = {
    student: { title: 'Delete student?', path: function (id) { return '/students/' + id; }, onDeleted: function () { showToast('Student deleted.'); loadStudents(); } },
    course: { title: 'Delete course?', path: function (id) { return '/courses/' + id; }, onDeleted: function () { showToast('Course deleted.'); loadCourses(); } },
    enrollment: { title: 'Remove enrollment?', path: function (id) { return '/enrollments/' + id; }, onDeleted: function () { showToast('Enrollment removed.'); reRunLastLookup(); } }
  };

  var pendingDelete = null;

  function openConfirmDelete(type, id, message) {
    pendingDelete = { type: type, id: id };
    qs('confirm-modal-title').textContent = DELETE_TYPES[type].title;
    qs('confirm-modal-text').textContent = message;
    qs('confirm-modal-backdrop').hidden = false;
  }

  qs('btn-cancel-delete').addEventListener('click', function () {
    qs('confirm-modal-backdrop').hidden = true;
    pendingDelete = null;
  });

  qs('btn-confirm-delete').addEventListener('click', function () {
    if (!pendingDelete) return;
    var config = DELETE_TYPES[pendingDelete.type];
    request('DELETE', config.path(pendingDelete.id)).then(function (res) {
      qs('confirm-modal-backdrop').hidden = true;
      if (res.ok) {
        config.onDeleted();
      } else {
        showToast(res.body && res.body.message ? res.body.message : 'Delete failed.', true);
      }
      pendingDelete = null;
    });
  });

  // ---------- Courses ----------

  var coursesCache = [];

  function loadCourses() {
    request('GET', '/courses').then(function (res) {
      var body = qs('courses-body');
      if (!res.ok) {
        body.innerHTML = '<tr><td colspan="4" class="empty">Failed to load courses.</td></tr>';
        return;
      }
      var courses = res.body;
      coursesCache = courses;
      body.innerHTML = courses.length
        ? courses.map(function (c) {
            return '<tr>' +
              '<td>' + c.id + '</td><td>' + escapeHtml(c.title) + '</td><td>' + c.capacity + '</td>' +
              '<td class="row-actions">' +
                '<button data-action="edit" data-id="' + c.id + '">Edit</button>' +
                '<button data-action="delete" class="delete" data-id="' + c.id + '" data-title="' + escapeHtml(c.title) + '">Delete</button>' +
              '</td>' +
            '</tr>';
          }).join('')
        : '<tr><td colspan="4" class="empty">No courses yet.</td></tr>';
    });
  }

  function openCourseModal(id) {
    var backdrop = qs('course-modal-backdrop');
    qs('course-form').reset();
    qs('course-title-error').textContent = '';
    qs('course-capacity-error').textContent = '';
    qs('course-id').value = id || '';
    qs('course-modal-title').textContent = id ? 'Edit Course' : 'Add Course';

    if (id) {
      var course = coursesCache.filter(function (c) { return String(c.id) === String(id); })[0];
      if (course) {
        qs('course-title').value = course.title;
        qs('course-capacity').value = course.capacity;
      }
    } else {
      qs('course-capacity').value = '30';
    }
    backdrop.hidden = false;
  }

  qs('btn-new-course').addEventListener('click', function () { openCourseModal(null); });
  qs('btn-cancel-course').addEventListener('click', function () { qs('course-modal-backdrop').hidden = true; });

  qs('courses-body').addEventListener('click', function (e) {
    var btn = e.target.closest('button[data-action]');
    if (!btn) return;
    var id = btn.dataset.id;
    if (btn.dataset.action === 'edit') {
      openCourseModal(id);
    } else if (btn.dataset.action === 'delete') {
      openConfirmDelete('course', id, 'This will permanently remove "' + btn.dataset.title + '" (id ' + id + ').');
    }
  });

  qs('course-form').addEventListener('submit', function (e) {
    e.preventDefault();
    qs('course-title-error').textContent = '';
    qs('course-capacity-error').textContent = '';

    var id = qs('course-id').value;
    var payload = { title: qs('course-title').value, capacity: parseInt(qs('course-capacity').value, 10) };
    var method = id ? 'PUT' : 'POST';
    var path = id ? '/courses/' + id : '/courses';

    request(method, path, payload).then(function (res) {
      if (res.ok) {
        qs('course-modal-backdrop').hidden = true;
        showToast(id ? 'Course updated.' : 'Course created.');
        loadCourses();
      } else if (res.status === 400) {
        var errs = fieldErrors(res.body);
        if (errs.title) qs('course-title-error').textContent = errs.title;
        if (errs.capacity) qs('course-capacity-error').textContent = errs.capacity;
      } else {
        showToast(res.body && res.body.message ? res.body.message : 'Something went wrong.', true);
      }
    });
  });

  // ---------- Enrollments ----------

  function loadDropdowns() {
    request('GET', '/students?page=0&size=200').then(function (res) {
      if (!res.ok) return;
      var sel = qs('enroll-student');
      sel.innerHTML = res.body.content.map(function (s) {
        return '<option value="' + s.id + '">' + s.id + ' — ' + escapeHtml(s.name) + '</option>';
      }).join('');
    });
    request('GET', '/courses').then(function (res) {
      if (!res.ok) return;
      var sel = qs('enroll-course');
      sel.innerHTML = res.body.map(function (c) {
        return '<option value="' + c.id + '">' + c.id + ' — ' + escapeHtml(c.title) + '</option>';
      }).join('');
    });
  }

  qs('enroll-form').addEventListener('submit', function (e) {
    e.preventDefault();
    var studentId = qs('enroll-student').value;
    var courseId = qs('enroll-course').value;
    if (!studentId || !courseId) {
      showToast('Add at least one student and one course first.', true);
      return;
    }
    request('POST', '/enrollments?studentId=' + studentId + '&courseId=' + courseId).then(function (res) {
      if (res.ok) {
        showToast('Enrolled successfully.');
      } else {
        showToast(res.body && res.body.message ? res.body.message : 'Enrollment failed.', true);
      }
    });
  });

  var lastLookup = null; // { kind: 'student'|'course', id }

  function renderEnrollments(list, emptyMessage) {
    var el = qs('enrollment-results');
    if (!list.length) {
      el.innerHTML = '<li class="empty-msg">' + emptyMessage + '</li>';
      return;
    }
    el.innerHTML = list.map(function (e) {
      return '<li>' +
        '<span><b>' + escapeHtml(e.studentName) + '</b> (id ' + e.studentId + ')</span>' +
        '<span>' + escapeHtml(e.courseTitle) + ' (id ' + e.courseId + ')</span>' +
        '<span class="row-actions"><button data-action="delete" class="delete" data-id="' + e.id + '">Un-enroll</button></span>' +
      '</li>';
    }).join('');
  }

  function reRunLastLookup() {
    if (!lastLookup) return;
    var path = '/enrollments/' + lastLookup.kind + '/' + lastLookup.id;
    var emptyMessage = 'No enrollments for ' + lastLookup.kind + ' ' + lastLookup.id + '.';
    request('GET', path).then(function (res) {
      if (res.ok) renderEnrollments(res.body, emptyMessage);
    });
  }

  qs('btn-lookup-student').addEventListener('click', function () {
    var id = qs('lookup-student-id').value;
    if (!id) return;
    lastLookup = { kind: 'student', id: id };
    request('GET', '/enrollments/student/' + id).then(function (res) {
      if (res.ok) renderEnrollments(res.body, 'No enrollments for student ' + id + '.');
      else showToast('Lookup failed.', true);
    }).catch(function () {
      showToast('Lookup failed.', true);
    });
  });

  qs('btn-lookup-course').addEventListener('click', function () {
    var id = qs('lookup-course-id').value;
    if (!id) return;
    lastLookup = { kind: 'course', id: id };
    request('GET', '/enrollments/course/' + id).then(function (res) {
      if (res.ok) renderEnrollments(res.body, 'No enrollments for course ' + id + '.');
      else showToast('Lookup failed.', true);
    }).catch(function () {
      showToast('Lookup failed.', true);
    });
  });

  qs('enrollment-results').addEventListener('click', function (e) {
    var btn = e.target.closest('button[data-action="delete"]');
    if (!btn) return;
    openConfirmDelete('enrollment', btn.dataset.id, 'This will remove enrollment id ' + btn.dataset.id + '.');
  });

  // ---------- Init ----------

  loadStudents();
  loadCourses();
})();
