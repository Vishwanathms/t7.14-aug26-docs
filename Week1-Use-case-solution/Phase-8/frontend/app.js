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

  var studentsState = { page: 0, size: 8, searchName: null };

  function loadStudents() {
    var path = studentsState.searchName
      ? '/students/search?name=' + encodeURIComponent(studentsState.searchName) + '&page=' + studentsState.page + '&size=' + studentsState.size
      : '/students?page=' + studentsState.page + '&size=' + studentsState.size;

    request('GET', path).then(function (res) {
      var body = qs('students-body');
      if (!res.ok) {
        body.innerHTML = '<tr><td colspan="4" class="empty">Failed to load students.</td></tr>';
        return;
      }
      var page = res.body;
      if (!page.content.length) {
        body.innerHTML = '<tr><td colspan="4" class="empty">No students found.</td></tr>';
      } else {
        body.innerHTML = page.content.map(function (s) {
          return '<tr>' +
            '<td>' + s.id + '</td>' +
            '<td>' + escapeHtml(s.name) + '</td>' +
            '<td>' + escapeHtml(s.email) + '</td>' +
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

  qs('students-prev').addEventListener('click', function () {
    if (studentsState.page > 0) { studentsState.page--; loadStudents(); }
  });
  qs('students-next').addEventListener('click', function () {
    studentsState.page++; loadStudents();
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
      openConfirmDelete(id, btn.dataset.name);
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

  // Delete confirmation

  var pendingDeleteId = null;

  function openConfirmDelete(id, name) {
    pendingDeleteId = id;
    qs('confirm-modal-text').textContent = 'This will permanently remove "' + name + '" (id ' + id + ').';
    qs('confirm-modal-backdrop').hidden = false;
  }

  qs('btn-cancel-delete').addEventListener('click', function () {
    qs('confirm-modal-backdrop').hidden = true;
    pendingDeleteId = null;
  });

  qs('btn-confirm-delete').addEventListener('click', function () {
    if (!pendingDeleteId) return;
    request('DELETE', '/students/' + pendingDeleteId).then(function (res) {
      qs('confirm-modal-backdrop').hidden = true;
      if (res.ok) {
        showToast('Student deleted.');
        loadStudents();
      } else {
        showToast(res.body && res.body.message ? res.body.message : 'Delete failed.', true);
      }
      pendingDeleteId = null;
    });
  });

  // ---------- Courses ----------

  function loadCourses() {
    request('GET', '/courses').then(function (res) {
      var body = qs('courses-body');
      if (!res.ok) {
        body.innerHTML = '<tr><td colspan="3" class="empty">Failed to load courses.</td></tr>';
        return;
      }
      var courses = res.body;
      body.innerHTML = courses.length
        ? courses.map(function (c) {
            return '<tr><td>' + c.id + '</td><td>' + escapeHtml(c.title) + '</td><td>' + c.capacity + '</td></tr>';
          }).join('')
        : '<tr><td colspan="3" class="empty">No courses yet.</td></tr>';
    });
  }

  qs('btn-new-course').addEventListener('click', function () {
    qs('course-form').reset();
    qs('course-title-error').textContent = '';
    qs('course-capacity-error').textContent = '';
    qs('course-capacity').value = '30';
    qs('course-modal-backdrop').hidden = false;
  });
  qs('btn-cancel-course').addEventListener('click', function () { qs('course-modal-backdrop').hidden = true; });

  qs('course-form').addEventListener('submit', function (e) {
    e.preventDefault();
    qs('course-title-error').textContent = '';
    qs('course-capacity-error').textContent = '';
    var payload = { title: qs('course-title').value, capacity: parseInt(qs('course-capacity').value, 10) };
    request('POST', '/courses', payload).then(function (res) {
      if (res.ok) {
        qs('course-modal-backdrop').hidden = true;
        showToast('Course created.');
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

  function renderEnrollments(list, emptyMessage) {
    var el = qs('enrollment-results');
    if (!list.length) {
      el.innerHTML = '<li class="empty-msg">' + emptyMessage + '</li>';
      return;
    }
    el.innerHTML = list.map(function (e) {
      return '<li><span><b>' + escapeHtml(e.studentName) + '</b> (id ' + e.studentId + ')</span>' +
             '<span>' + escapeHtml(e.courseTitle) + ' (id ' + e.courseId + ')</span></li>';
    }).join('');
  }

  qs('btn-lookup-student').addEventListener('click', function () {
    var id = qs('lookup-student-id').value;
    if (!id) return;
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
    request('GET', '/enrollments/course/' + id).then(function (res) {
      if (res.ok) renderEnrollments(res.body, 'No enrollments for course ' + id + '.');
      else showToast('Lookup failed.', true);
    }).catch(function () {
      showToast('Lookup failed.', true);
    });
  });

  // ---------- Init ----------

  loadStudents();
  loadCourses();
})();
