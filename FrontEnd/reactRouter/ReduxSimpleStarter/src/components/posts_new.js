import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux'
import { createPost } from "../actions";


class PostsNew extends Component {

  renderField(field) {
    return (
      <div className="form-group has-danger">
        <label>{field.mylabel}</label>
        <input
          className="form-control"
          {...field.input}
        />
        <div className="text-help">
          {field.meta.touched ? field.meta.error: '' /*field has 3 meta states: pristine, touched, invalid*/}
        </div>
      </div>
    )
  }

  onSubmit(values) {
    this.props.createPost(values); // cross origin resource sharing. The OPTIONS call.
    // This is for security reasons.
  }

  render() {
    const { handleSubmit } = this.props
    return (
      <div>
        <form onSubmit={handleSubmit(this.onSubmit.bind(this))}>
          <Field
            name="title"
            mylabel="Title for Post"
            component={this.renderField}
          />
          <Field
            name="categories"
            mylabel="Categories"
            component={this.renderField}
          />
          <Field
            name="content"
            mylabel="Post Content"
            component={this.renderField}
          />
          <button type="submit" className="btn btn-primary">Submit</button>
          <Link to="/" className="btn btn-danger">Cancel</Link>
        </form>
      </div>
    );
  }
}

function validate(values) {
  // this is called whenever a user tries to submit a form
  const errors = {}; // keyword
  if(!values.title) {
    errors.title = "Enter a title";
  }
  if(!values.categories) {
    errors.categories = "Enter a categories";
  }
  if(!values.content) {
    errors.content = "Enter a content";
  }
  // validate the input from values

  // if errors is empty, then form is fine to submit
  // if errors has any prop then it'll fail.
  return errors;

  // this automatically updates field.meta.error
}

export default reduxForm({
  validate: validate,
  form: 'PostsNewForm'
})(
  connect(null, { createPost })(PostsNew)
);
// very similar to the connect function.
// the string assigned to this form property should be unique

